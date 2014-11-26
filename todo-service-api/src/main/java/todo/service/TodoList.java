package todo.service;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public class TodoList implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * @required update
	 */
	private String id;

	/**
	 * @required create/update
	 */
	private String name;

	/**
	 * @optional
	 */
	private String description;

	public TodoList()
	{
		super();
	}

	public TodoList(final String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
