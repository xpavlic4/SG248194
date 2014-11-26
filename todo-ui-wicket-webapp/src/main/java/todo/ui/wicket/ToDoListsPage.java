package todo.ui.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import todo.service.TodoList;

/**
 * Lists all existing todo lists.
 *
 * @author <a href="http://pl.ibm.com/">Marek Zajac</a>
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@SuppressWarnings("serial")
public class ToDoListsPage extends AbstractBasePage
{
	private static final Logger LOG = LoggerFactory.getLogger(ToDoListsPage.class);

	private final TodoList newTodoList = new TodoList();

	public ToDoListsPage()
	{
		/*
		 * add number of existing lists
		 */
		add(new Label("lists-count", new AbstractReadOnlyModel<Object>()
			{
				@Override
				public Object getObject()
				{
					return backend.getLists().size() /*TODO optimize*/;
				}
			}));

		/*
		 * add list view
		 */
		add(new ListView<TodoList>("todo-lists", new AbstractReadOnlyModel<List<TodoList>>()
			{
				@Override
				public List<TodoList> getObject()
				{
					return new ArrayList<TodoList>(backend.getLists());
				}
			})
			{
				@Override
				protected void populateItem(final ListItem<TodoList> item)
				{
					final TodoList todoList = item.getModelObject();

					item.add(new Label("list-items-count", backend.getItems(todoList.getId()).size()) /* TODO optimize */);
					item.add(new Link<String>("list-link")
						{
							@Override
							public void onClick()
							{
								ToDoListItemsPage.redirectTo(todoList.getId(), this);
							}
						});
					item.add(new Label("list-name", todoList.getName()));
					item.add(new Link<String>("list-edit")
						{
							@Override
							public void onClick()
							{
								ToDoListEditPage.redirectTo(todoList.getId(), this);
							}
						});
					item.add(new Link<String>("list-remove")
						{
							{
								add(new AttributeModifier("onclick", "return confirm('are you sure?');"));
							}

							@Override
							public void onClick()
							{
								backend.removeList(todoList.getId());
								setResponsePage(ToDoListsPage.class);
							}
						});
					item.add(new Label("list-description", todoList.getDescription()));
				}
			});

		/*
		 * add feedback panel
		 */
		add(new FeedbackPanel("feedbackPanel"));

		/*
		 * add new list input form
		 */
		add(new Form<TodoList>("new-todo-list-form", new CompoundPropertyModel<TodoList>(newTodoList))
			{
				{
					add(new TextField<String>("name").setRequired(true));
					add(new TextArea<String>("description"));
					final Button submitButton = new Button("submitButton")
						{
							@Override
							public void onSubmit()
							{
								LOG.debug("Creating new TODO list[name={}]...", newTodoList.getName());
								backend.createList(newTodoList);
								getSession().info(newTodoList.getName() + " list has been created.");
								setResponsePage(ToDoListsPage.class);
							}
						};
					add(submitButton);
				}
			});
	}
}
