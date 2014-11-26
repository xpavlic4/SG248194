package todo.service;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public class TodoListItem implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * @required update
	 */
	private String id;

	/**
	 * @required create/update
	 */
	private String listId;

	/**
	 * @required create/update
	 */
	private String description;

	/**
	 * @optional
	 */
	private Date dueDate;

	/**
	 * @optional
	 */
	private boolean isDone;

	public TodoListItem()
	{
		super();
	}

	public TodoListItem(final String listId, final String description)
	{
		this.listId = listId;
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public Date getDueDate()
	{
		return dueDate;
	}

	public String getId()
	{
		return id;
	}

	public String getListId()
	{
		return listId;
	}

	public boolean isDone()
	{
		return isDone;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setDone(final boolean isDone)
	{
		this.isDone = isDone;
	}

	public void setDueDate(final Date dueDate)
	{
		this.dueDate = dueDate;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setListId(final String listId)
	{
		this.listId = listId;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
