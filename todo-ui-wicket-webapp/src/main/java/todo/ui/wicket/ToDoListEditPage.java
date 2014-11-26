package todo.ui.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import todo.service.TodoList;

/**
 * @author <a href="http://pl.ibm.com/">Marek Zajac</a>
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@SuppressWarnings("serial")
public class ToDoListEditPage extends AbstractBasePage
{
	private static final Logger LOG = LoggerFactory.getLogger(ToDoListsPage.class);

	public static void redirectTo(String targetTodoListId, Component from)
	{
		from.setResponsePage(ToDoListEditPage.class, new PageParameters().add("todoListId", targetTodoListId));
	}

	public ToDoListEditPage(PageParameters params)
	{
		final String todoListId = params.get("todoListId").toString();

		final TodoList todoList = backend.getListById(todoListId);
		/*
		 * add new list input form
		 */
		add(new Form<TodoList>("edit-todo-list-form", new CompoundPropertyModel<TodoList>(todoList))
			{
				{
					add(new TextField<String>("name").setRequired(true));
					add(new TextArea<String>("description"));
					final Button submitButton = new Button("submitButton")
						{
							@Override
							public void onSubmit()
							{
								LOG.debug("Creating new TODO list[name={}]...", todoList.getName());
								backend.updateList(todoList);
								getSession().info(todoList.getName() + " list has been updated.");
								setResponsePage(ToDoListsPage.class);
							}
						};
					add(submitButton);
				}
			});
	}
}
