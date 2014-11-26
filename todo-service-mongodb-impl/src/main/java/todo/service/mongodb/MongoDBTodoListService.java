package todo.service.mongodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import org.bson.types.ObjectId;

import todo.service.TodoList;
import todo.service.TodoListItem;
import todo.service.TodoListService;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * MongoDB implementation of {@link TodoListService}
 *
 * @author <a href="http://cn.ibm.com/">Iris Ding</a>
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@javax.inject.Singleton
public class MongoDBTodoListService implements TodoListService
{
	public static final String MONGO_DB_JNDI_NAME = "mongo/TODOLIST";

	@Resource(name = MONGO_DB_JNDI_NAME)
	private DB db;

	@SuppressWarnings("static-method")
	private DBObject _contains(final String searchFor)
	{
		return new BasicDBObject("$regex", ".*" + searchFor + ".*").append("$options", "i");
	}

	private DBCollection _getCollectionWithItems()
	{
		return db.getCollection("items");
	}

	private DBCollection _getCollectionWithLists()
	{
		return db.getCollection("lists");
	}

	private DBObject _getItemById(final String itemId)
	{
		final DBObject query = new BasicDBObject("_id", new ObjectId(itemId));
		final DBObject result = _getCollectionWithItems().findOne(query);
		if (result == null) throw new IllegalArgumentException("Unknown itemId " + itemId);
		return result;
	}

	private DBObject _getListById(final String listId)
	{
		final DBObject query = new BasicDBObject("_id", new ObjectId(listId));
		final DBObject result = _getCollectionWithLists().findOne(query);
		if (result == null) throw new IllegalArgumentException("Unknown listId " + listId);
		return result;
	}

	@SuppressWarnings("static-method")
	private TodoListItem _toItem(final DBObject record)
	{
		final TodoListItem result = new TodoListItem();
		result.setId(record.get("_id").toString());
		result.setListId(record.get("_listId").toString());
		result.setDescription((String) record.get("description"));
		result.setDueDate((Date) record.get("duedate"));
		result.setDone((Boolean) record.get("isdone"));
		return result;
	}

	@SuppressWarnings("static-method")
	private TodoList _toList(final DBObject record)
	{
		final TodoList result = new TodoList();
		result.setId(record.get("_id").toString());
		result.setName((String) record.get("name"));
		result.setDescription((String) record.get("description"));
		return result;
	}

	@Override
	public String createItem(final TodoListItem item)
	{
		if (item == null) throw new IllegalArgumentException("[item] must not be null");
		if (item.getId() != null) throw new IllegalArgumentException("[item.id] must be null");
		if (item.getListId() == null) throw new IllegalArgumentException("[item.listId] must not be null");
		if (item.getDescription() == null) throw new IllegalArgumentException("[item.description] must not be null");

		final DBObject record = new BasicDBObject();
		final ObjectId id = ObjectId.get();
		record.put("_id", id);
		record.put("_listId", new ObjectId(item.getListId()));
		record.put("description", item.getDescription());
		record.put("duedate", item.getDueDate());
		record.put("isdone", item.isDone());
		_getCollectionWithItems().insert(record);
		return id.toString();
	}

	@Override
	public String createList(final TodoList list)
	{
		if (list == null) throw new IllegalArgumentException("[list] must not be null");
		if (list.getId() != null) throw new IllegalArgumentException("[list.id] must be null");
		if (list.getName() == null) throw new IllegalArgumentException("[list.name] must not be null");

		final DBObject record = new BasicDBObject();
		final ObjectId id = ObjectId.get();
		record.put("_id", id);
		record.put("name", list.getName());
		record.put("description", list.getDescription());
		_getCollectionWithLists().insert(record);
		return id.toString();
	}

	@Override
	public Collection<TodoListItem> findItems(final String listId, final String searchString)
	{
		if (searchString == null) return Collections.emptyList();

		final DBObject query = new BasicDBObject("$and", Arrays.asList( //
				new BasicDBObject("_listId", new ObjectId(listId)), //
				new BasicDBObject("description", _contains(searchString))//
				));

		final DBCursor records = _getCollectionWithItems().find(query);
		try
		{
			final List<TodoListItem> result = new ArrayList<TodoListItem>();
			for (final DBObject record : records)
				result.add(_toItem(record));
			return result;
		}
		finally
		{
			records.close();
		}
	}

	@Override
	public Collection<TodoListItem> findItemsInAllLists(final String searchString)
	{
		if (searchString == null) return Collections.emptyList();

		final DBObject query = new BasicDBObject("description", _contains(searchString));

		final DBCursor records = _getCollectionWithItems().find(query);
		try
		{
			final List<TodoListItem> result = new ArrayList<TodoListItem>();
			for (final DBObject record : records)
				result.add(_toItem(record));
			return result;
		}
		finally
		{
			records.close();
		}
	}

	@Override
	public Collection<TodoList> findLists(final String searchString)
	{
		if (searchString == null) return Collections.emptyList();

		final DBObject query = new BasicDBObject("$or", Arrays.asList( //
				new BasicDBObject("name", _contains(searchString)), //
				new BasicDBObject("description", _contains(searchString))//
				));

		final DBCursor records = _getCollectionWithLists().find(query);
		try
		{
			final List<TodoList> result = new ArrayList<TodoList>();
			for (final DBObject record : records)
				result.add(_toList(record));
			return result;
		}
		finally
		{
			records.close();
		}
	}

	@Override
	public TodoListItem getItemById(final String itemId)
	{
		return _toItem(_getItemById(itemId));
	}

	@Override
	public Collection<TodoListItem> getItems(final String listId)
	{
		if (listId == null) throw new IllegalArgumentException("[listId] must not be null");

		final DBObject query = new BasicDBObject("_listId", new ObjectId(listId));

		final DBCursor records = _getCollectionWithItems().find(query);
		try
		{
			final List<TodoListItem> result = new ArrayList<TodoListItem>();
			for (final DBObject record : records)
				result.add(_toItem(record));
			return result;
		}
		finally
		{
			records.close();
		}
	}

	@Override
	public TodoList getListById(final String listId)
	{
		if (listId == null) throw new IllegalArgumentException("[listId] must not be null");

		return _toList(_getListById(listId));
	}

	@Override
	public Collection<TodoList> getLists()
	{
		final DBCursor records = _getCollectionWithLists().find();
		try
		{
			final Collection<TodoList> result = new ArrayList<TodoList>();
			for (final DBObject record : records)
				result.add(_toList(record));
			return result;
		}
		finally
		{
			records.close();
		}
	}

	@Override
	public void removeItem(final String itemId)
	{
		if (itemId == null) throw new IllegalArgumentException("[itemId] must not be null");

		_getCollectionWithItems().remove(_getItemById(itemId));
	}

	@Override
	public void removeList(final String listId)
	{
		if (listId == null) throw new IllegalArgumentException("[listId] must not be null");

		_getCollectionWithLists().remove(_getListById(listId));
	}

	@Override
	public void updateItem(final TodoListItem item)
	{
		if (item == null) throw new IllegalArgumentException("[item] must not be null");
		if (item.getId() == null) throw new IllegalArgumentException("[item.id] must not be null");
		if (item.getListId() == null) throw new IllegalArgumentException("[item.listId] must not be null");
		if (item.getDescription() == null) throw new IllegalArgumentException("[item.description] must not be null");

		final DBObject record = _getItemById(item.getId());
		record.put("_listId", new ObjectId(item.getListId()));
		record.put("description", item.getDescription());
		record.put("duedate", item.getDueDate());
		record.put("isdone", item.isDone());
		_getCollectionWithItems().save(record);
	}

	@Override
	public void updateList(final TodoList list)
	{
		if (list == null) throw new IllegalArgumentException("[list] must not be null");
		if (list.getId() == null) throw new IllegalArgumentException("[list.id] must not be null");
		if (list.getName() == null) throw new IllegalArgumentException("[list.name] must not be null");

		final DBObject record = _getListById(list.getId());
		record.put("name", list.getName());
		record.put("description", list.getDescription());
		_getCollectionWithLists().save(record);
	}
}
