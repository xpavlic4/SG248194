package todo.ui.rest.jaxrs.resources;

import static todo.ui.rest.jaxrs.TodoListRestApplication.LIST_ID;
import static todo.ui.rest.jaxrs.TodoListRestApplication.SEARCH_STRING;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import todo.service.TodoList;
import todo.service.TodoListItem;
import todo.service.TodoListService;

/**
 * @author <a href="http://ibm.com.au/">Alexander Poga</a>
 */
@Singleton
@Path("lists")
@Produces(MediaType.APPLICATION_JSON)
public class List
{
	@EJB
	private TodoListService realService;

	@POST
	public String createList(final TodoList list)
	{
		return realService.createList(list);
	}

	@GET
	@Path("{" + LIST_ID + "}/items")
	public Collection<TodoListItem> findItems(@PathParam(LIST_ID) final String listId, @QueryParam(SEARCH_STRING) final String searchString)
	{
		if (searchString == null) return realService.getItems(listId);
		return realService.findItems(listId, searchString);
	}

	@GET
	public Collection<TodoList> findLists(@QueryParam(SEARCH_STRING) final String searchString)
	{
		if (searchString == null) return realService.getLists();
		return realService.findLists(searchString);
	}

	@GET
	@Path("{" + LIST_ID + "}")
	public TodoList getListById(@PathParam(LIST_ID) final String listId)
	{
		return realService.getListById(listId);
	}

	@DELETE
	@Path("{" + LIST_ID + "}")
	public void removeList(@PathParam(LIST_ID) final String listId)
	{
		realService.removeList(listId);
	}

	@PUT
	public void updateList(final TodoList list)
	{
		realService.updateList(list);
	}
}
