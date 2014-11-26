package todo.service;

import java.util.Collection;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public interface TodoListService
{
	/**
	 * @return the id of the newly created todo list item
	 *
	 * @throws IllegalArgumentException if item.id is not null
	 * @throws IllegalArgumentException if item.listId references a non-existing todo list
	 */
	String createItem(TodoListItem item);

	/**
	 * @return the id of the newly created todo list
	 *
	 * @throws IllegalArgumentException if list.id is not null
	 */
	String createList(TodoList list);

	Collection<TodoListItem> findItemsInAllLists(String searchFor);

	Collection<TodoListItem> findItems(String listId, String searchFor);

	Collection<TodoList> findLists(String searchFor);

	/**
	 * @return all items of the todo list with the given id
	 */
	Collection<TodoListItem> getItems(String listId);

	/**
	* @throws IllegalArgumentException if no item with the given id exists
	 */
	TodoListItem getItemById(String itemId);

	/**
	* @throws IllegalArgumentException if no list with the given id exists
	 */
	TodoList getListById(String listId);

	/**
	 * @return all existing todo lists
	 */
	Collection<TodoList> getLists();

	/**
	 * @throws IllegalArgumentException if item.id references a non-existing todo list item
	 * @throws IllegalArgumentException if item.listId references a non-existing todo list
	 */
	void updateItem(TodoListItem item);

	/**
	 * @throws IllegalArgumentException if item.id references a non-existing todo list
	 */
	void updateList(TodoList list);
	/**
	* @throws IllegalArgumentException if no item with the given id exists
	 */
	void removeItem(String itemId);

	/**
	* @throws IllegalArgumentException if no list with the given id exists
	 */
	void removeList(String listId);
}
