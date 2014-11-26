package todo.ui.rest.jaxrs;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author <a href="http://ibm.com.au/">Alexander Poga</a>
 */
@ApplicationPath("api")
public class TodoListRestApplication extends Application
{
	// Constants to use for resource parameters in request Paths
	public static final String ITEM_ID = "itemId";
	public static final String LIST_ID = "listId";
	public static final String SEARCH_STRING = "searchString";
}
