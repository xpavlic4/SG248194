package todo.service;

import todo.service.inmemory.InMemoryTodoListService;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public class InMemoryTodoListServiceTest extends AbstractTodoListServiceTest
{
	public void testInMemoryTodoListService()
	{
		testImplementation(new InMemoryTodoListService());
	}
}
