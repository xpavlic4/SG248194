package todo.ui.rest.jaxrs.resources;

import static todo.ui.rest.jaxrs.TodoListRestApplication.ITEM_ID;
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

import todo.service.TodoListItem;
import todo.service.TodoListService;

/**
 * @author <a href="http://ibm.com.au/">Alexander Poga</a>
 */
@Singleton
@Path("items")
@Produces(MediaType.APPLICATION_JSON)
public class Item
{
	@EJB
	private TodoListService realService;

	@GET
	public Collection<TodoListItem> findItemsInAllLists(@QueryParam(SEARCH_STRING) final String searchString)
	{
		return realService.findItemsInAllLists(searchString);
	}

	@GET
	@Path("{" + ITEM_ID + "}")
	public TodoListItem getItemById(@PathParam(ITEM_ID) final String itemId)
	{
		return realService.getItemById(itemId);
	}

	@PUT
	public void updateItem(final TodoListItem item)
	{
		realService.updateItem(item);
	}

	@POST
	public String createItem(final TodoListItem item)
	{
		return realService.createItem(item);
	}

	@DELETE
	@Path("{" + ITEM_ID + "}")
	public void removeItem(@PathParam(ITEM_ID) final String itemId)
	{
		realService.removeItem(itemId);
	}
}
