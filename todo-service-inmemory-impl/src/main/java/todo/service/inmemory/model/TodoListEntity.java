package todo.service.inmemory.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public class TodoListEntity
{
	public String id;
	public String name;
	public String description;
	public final List<TodoListItemEntity> items = new ArrayList<TodoListItemEntity>();
}
