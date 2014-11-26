package todo.service;

import junit.framework.TestCase;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public abstract class AbstractTodoListServiceTest extends TestCase
{
	protected void testImplementation(final TodoListService srv)
	{
		try
		{
			srv.createList(null);
			fail();
		}
		catch (final IllegalArgumentException ex)
		{
			assertEquals("[list] must not be null", ex.getMessage());
		}

		try
		{
			final TodoList list1 = new TodoList();
			srv.createList(list1);
			fail();
		}
		catch (final IllegalArgumentException ex)
		{
			assertEquals("[list.name] must not be null", ex.getMessage());
		}

		// create new list
		final TodoList newShoppingList = new TodoList();
		newShoppingList.setName("Stuff to buy");
		final String shoppingListId = srv.createList(newShoppingList);
		assertNotNull(shoppingListId);

		// retrieve list from service
		TodoList shoppingList = srv.getListById(shoppingListId);
		assertNotSame(newShoppingList, shoppingList);
		assertNotNull(shoppingList);
		assertNotNull(shoppingList.getId());
		assertEquals("Stuff to buy", shoppingList.getName());

		// update list from service
		shoppingList.setDescription("This list contains things I want to buy.");
		srv.updateList(shoppingList);
		shoppingList = srv.getListById(shoppingListId);
		assertEquals("This list contains things I want to buy.", shoppingList.getDescription());

		try
		{
			final TodoListItem item = new TodoListItem();
			srv.createItem(item);
			fail();
		}
		catch (final IllegalArgumentException ex)
		{
			assertEquals("[item.listId] must not be null", ex.getMessage());
		}

		srv.createItem(new TodoListItem(shoppingListId, "dark bread"));

		TodoListItem item = srv.getItems(shoppingListId).iterator().next();
		item.setDescription("dark bread (whole grains)");
		item.setDone(true);
		srv.updateItem(item);
		item = srv.getItems(shoppingListId).iterator().next();
		assertTrue(item.isDone());
		assertEquals("dark bread (whole grains)", item.getDescription());

		srv.createItem(new TodoListItem(shoppingListId, "milk"));
		srv.createItem(new TodoListItem(shoppingListId, "1 bottle of red wine"));
		assertEquals(3, srv.getItems(shoppingListId).size());
		srv.removeItem(srv.getItems(shoppingListId).iterator().next().getId());
		assertEquals(2, srv.getItems(shoppingListId).size());

		final String whishListId = srv.createList(new TodoList("Whishlist"));
		assertEquals(2, srv.getLists().size());

		srv.createItem(new TodoListItem(whishListId, "red car"));
		srv.createItem(new TodoListItem(whishListId, "laptop"));
		srv.createItem(new TodoListItem(whishListId, "book"));

		assertEquals(2, srv.findItemsInAllLists("red").size());
		assertEquals(3, srv.getItems(whishListId).size());
		assertEquals(1, srv.findItems(whishListId, "red").size());
	}
}
