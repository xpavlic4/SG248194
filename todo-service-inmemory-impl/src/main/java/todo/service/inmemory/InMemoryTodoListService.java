package todo.service.inmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import todo.service.TodoList;
import todo.service.TodoListItem;
import todo.service.TodoListService;
import todo.service.inmemory.model.TodoListEntity;
import todo.service.inmemory.model.TodoListItemEntity;

/**
 * Simple in-memory implementation of the {@link TodoListService}
 *
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@javax.ejb.Singleton
@javax.ejb.Local(TodoListService.class)
public class InMemoryTodoListService implements TodoListService
{
	private static final MapperFacade MAPPER_WITHOUT_ID;
	private static final MapperFacade MAPPER_WITH_ID;

	static
	{
		final MapperFactory factory1 = new DefaultMapperFactory.Builder().build();
		factory1.registerClassMap(factory1.classMap(TodoListEntity.class, TodoList.class).exclude("id").byDefault().toClassMap());
		factory1.registerClassMap(factory1.classMap(TodoListItemEntity.class, TodoListItem.class).exclude("id").byDefault().toClassMap());
		MAPPER_WITHOUT_ID = factory1.getMapperFacade();

		final MapperFactory factory2 = new DefaultMapperFactory.Builder().build();
		factory2.registerClassMap(factory2.classMap(TodoListEntity.class, TodoList.class).byDefault().toClassMap());
		factory2.registerClassMap(factory2.classMap(TodoListItemEntity.class, TodoListItem.class).field("list.id", "listId").byDefault()
				.toClassMap());
		MAPPER_WITH_ID = factory2.getMapperFacade();
	}

	private final ConcurrentMap<String, TodoListEntity> database = new ConcurrentHashMap<String, TodoListEntity>();
	private final AtomicLong idGenerator = new AtomicLong();

	private TodoListItemEntity _getItemEntity(final String itemId)
	{
		for (final TodoListEntity list : database.values())
			for (final TodoListItemEntity item : list.items)
				if (item.id.equals(itemId)) return item;
		throw new IllegalArgumentException("Item with ID" + itemId + " not found");
	}

	private TodoListEntity _getListEntity(final String listId)
	{
		final TodoListEntity list = database.get(listId);
		if (list == null) throw new IllegalArgumentException("List with ID" + listId + " not found");
		return list;
	}

	@Override
	public String createItem(final TodoListItem item)
	{
		if (item == null) throw new IllegalArgumentException("[item] must not be null");
		if (item.getId() != null) throw new IllegalArgumentException("[item.id] must be null");
		if (item.getListId() == null) throw new IllegalArgumentException("[item.listId] must not be null");
		if (item.getDescription() == null) throw new IllegalArgumentException("[item.description] must not be null");

		// create a corresponding entity object and copy the attribute values
		final TodoListItemEntity entity = MAPPER_WITHOUT_ID.map(item, TodoListItemEntity.class);
		// assign an ID
		entity.id = Long.toString(idGenerator.incrementAndGet());
		// add entity to corresponding list stored in in-memory database
		entity.setList(_getListEntity(item.getListId()));
		return entity.id;
	}

	@Override
	public String createList(final TodoList list)
	{
		if (list == null) throw new IllegalArgumentException("[list] must not be null");
		if (list.getId() != null) throw new IllegalArgumentException("[list.id] must be null");
		if (list.getName() == null) throw new IllegalArgumentException("[list.name] must not be null");

		// create a corresponding entity object and copy the attribute values
		final TodoListEntity copy = MAPPER_WITHOUT_ID.map(list, TodoListEntity.class);
		// assign an ID
		copy.id = Long.toString(idGenerator.incrementAndGet());
		// set description to empty string if null
		if (copy.description == null) copy.description = "";
		// add to in-memory storage
		database.put(copy.id, copy);
		return copy.id;
	}

	@Override
	public Collection<TodoListItem> findItems(final String listId, String searchFor)
	{
		if (searchFor == null) return Collections.emptyList();
		searchFor = searchFor.trim().toLowerCase();

		final List<TodoListItem> result = new ArrayList<>();
		for (final TodoListItemEntity item : _getListEntity(listId).items)
			if (item.description.toLowerCase().contains(searchFor)) result.add(MAPPER_WITH_ID.map(item, TodoListItem.class));
		return result;
	}

	@Override
	public Collection<TodoListItem> findItemsInAllLists(String searchFor)
	{
		if (searchFor == null) return Collections.emptyList();
		searchFor = searchFor.trim().toLowerCase();

		final List<TodoListItem> result = new ArrayList<>();
		for (final TodoListEntity entity : database.values())
			for (final TodoListItemEntity item : entity.items)
				if (item.description.toLowerCase().contains(searchFor)) result.add(MAPPER_WITH_ID.map(item, TodoListItem.class));
		return result;
	}

	@Override
	public Collection<TodoList> findLists(String searchFor)
	{
		if (searchFor == null) return Collections.emptyList();
		searchFor = searchFor.trim().toLowerCase();

		final List<TodoList> result = new ArrayList<>();
		for (final TodoListEntity entity : database.values())
			if (entity.name.toLowerCase().contains(searchFor) || entity.description.toLowerCase().contains(searchFor))
				result.add(MAPPER_WITH_ID.map(entity, TodoList.class));
		return result;
	}

	@Override
	public TodoListItem getItemById(final String itemId)
	{
		return MAPPER_WITH_ID.map(_getItemEntity(itemId), TodoListItem.class);
	}

	@Override
	public Collection<TodoListItem> getItems(final String listId)
	{
		if (listId == null) throw new IllegalArgumentException("[listId] must not be null");

		final List<TodoListItem> result = new ArrayList<>();
		for (final TodoListItemEntity item : _getListEntity(listId).items)
			result.add(MAPPER_WITH_ID.map(item, TodoListItem.class));
		return result;
	}

	@Override
	public TodoList getListById(final String listId)
	{
		return MAPPER_WITH_ID.map(_getListEntity(listId), TodoList.class);
	}

	@Override
	public Collection<TodoList> getLists()
	{
		final List<TodoList> result = new ArrayList<>();
		for (final TodoListEntity entity : database.values())
			result.add(MAPPER_WITH_ID.map(entity, TodoList.class));
		return result;
	}

	@Override
	public void removeItem(final String itemId)
	{
		if (itemId == null) throw new IllegalArgumentException("[itemId] must not be null");

		final TodoListItemEntity entity = _getItemEntity(itemId);
		entity.getList().items.remove(entity);
	}

	@Override
	public void removeList(final String listId)
	{
		if (listId == null) throw new IllegalArgumentException("[listId] must not be null");

		if (database.remove(listId) == null) throw new IllegalArgumentException("List with ID" + listId + " not found");
	}

	@Override
	public void updateItem(final TodoListItem item)
	{
		if (item == null) throw new IllegalArgumentException("[item] must not be null");
		if (item.getId() == null) throw new IllegalArgumentException("[item.id] must not be null");
		if (item.getListId() == null) throw new IllegalArgumentException("[item.listId] must not be null");
		if (item.getDescription() == null) throw new IllegalArgumentException("[item.description] must not be null");

		// ensure item.listId exists
		_getListEntity(item.getListId());

		MAPPER_WITHOUT_ID.map(item, _getItemEntity(item.getId()));
	}

	@Override
	public void updateList(final TodoList list)
	{
		if (list == null) throw new IllegalArgumentException("[list] must not be null");
		if (list.getId() == null) throw new IllegalArgumentException("[list.id] must not be null");
		if (list.getName() == null) throw new IllegalArgumentException("[list.name] must not be null");

		MAPPER_WITHOUT_ID.map(list, _getListEntity(list.getId()));
	}
}
