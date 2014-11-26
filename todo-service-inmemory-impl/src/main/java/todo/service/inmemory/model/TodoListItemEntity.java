package todo.service.inmemory.model;

import java.util.Date;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public class TodoListItemEntity
{
	public String id;
	public String description;
	public Date dueDate;
	public boolean done;

	private TodoListEntity list;

	public TodoListEntity getList()
	{
		return list;
	}

	/**
	 * Establishes bi-directional reference between this item and the given list
	 */
	public void setList(final TodoListEntity newList)
	{
		if (list != null) list.items.remove(this);
		list = newList;
		list.items.add(this);
	}
}
