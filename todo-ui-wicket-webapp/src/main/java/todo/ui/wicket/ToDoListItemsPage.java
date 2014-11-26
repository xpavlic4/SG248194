package todo.ui.wicket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import todo.service.TodoListItem;

/**
 * @author <a href="http://pl.ibm.com/">Marek Zajac</a>
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@SuppressWarnings("serial")
public class ToDoListItemsPage extends AbstractBasePage
{
	private static final Logger LOG = LoggerFactory.getLogger(ToDoListsPage.class);

	public static void redirectTo(String targetTodoListId, Component from)
	{
		from.setResponsePage(ToDoListItemsPage.class, new PageParameters().add("todoListId", targetTodoListId));
	}

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private final TodoListItem newTodoItem = new TodoListItem();

	public ToDoListItemsPage(PageParameters params)
	{
		final String todoListId = params.get("todoListId").toString();

		/*
		 * add feedback panel
		 */
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		add(feedbackPanel.setOutputMarkupId(true));

		/*
		 * add number of existing lists
		 */
		add(new Label("items-count", new AbstractReadOnlyModel<Object>()
			{
				@Override
				public Object getObject()
				{
					return backend.getItems(todoListId).size() /*TODO optimize*/;
				}
			}));

		/*
		 * add list view
		 */
		add(new ListView<TodoListItem>("items", new AbstractReadOnlyModel<List<TodoListItem>>()
			{
				@Override
				public List<TodoListItem> getObject()
				{
					return new ArrayList<TodoListItem>(backend.getItems(todoListId));
				}
			})
			{
				@Override
				protected void populateItem(final ListItem<TodoListItem> listItem)
				{
					final TodoListItem item = listItem.getModelObject();

					listItem.setOutputMarkupId(true);

					listItem.add(new AjaxCheckBox("isDone", new PropertyModel<Boolean>(item, "isDone"))
						{
							@Override
							protected void onUpdate(AjaxRequestTarget target)
							{
								LOG.debug("Toggling isDone property of item[id={}]...", item.getId());
								item.setDone(getModelObject());
								backend.updateItem(item);
								if (item.isDone())
									getSession().info("Item [" + item.getDescription() + "] has been marked as done.");
								else
									getSession().info("Item [" + item.getDescription() + "] has been marked as not-done.");
								target.add(listItem, feedbackPanel);
							}
						});

					listItem.add(new Label("item-description", item.getDescription()));

					listItem.add(new Link<String>("item-remove")
						{
							{
								add(new AttributeModifier("onclick", "return confirm('are you sure?');"));
							}

							@Override
							public void onClick()
							{
								backend.removeItem(item.getId());
								redirectTo(item.getListId(), this);
							}
						});

					if (item.getDueDate() == null)
						listItem.add(new Label("dueDate", ""));
					else
						listItem.add(new Label("dueDate", "Due: " + dateFormatter.format(item.getDueDate())));
				}
			});

		/*
		 * add new list input form
		 */
		add(new Form<TodoListItem>("new-todo-item-form", new CompoundPropertyModel<TodoListItem>(newTodoItem))
			{
				{
					add(new TextArea<String>("description").setRequired(true));
					final DateTextField date = new DateTextField("dueDate");
					date.add(new DatePicker().setAutoHide(true));
					add(date);
					final Button submitButton = new Button("submitButton")
						{
							@Override
							public void onSubmit()
							{
								LOG.debug("Creating new TODO list item[description={}]...", newTodoItem.getDescription());
								newTodoItem.setListId(todoListId);
								backend.createItem(newTodoItem);
								getSession().info(newTodoItem.getDescription() + " list has been saved");
								redirectTo(todoListId, this);
							}
						};
					add(submitButton);
				}
			});
	}
}
