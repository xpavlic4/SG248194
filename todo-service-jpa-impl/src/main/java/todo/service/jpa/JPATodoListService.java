package todo.service.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import todo.service.TodoList;
import todo.service.TodoListItem;
import todo.service.TodoListService;
import todo.service.jpa.model.TodoListEntity;
import todo.service.jpa.model.TodoListItemEntity;
import todo.service.jpa.repository.TodoListItemRepository;
import todo.service.jpa.repository.TodoListRepository;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@Service
@Transactional(readOnly = true)
public class JPATodoListService implements TodoListService
{
	private static final MapperFacade MAPPER_WITHOUT_ID;
	private static final MapperFacade MAPPER_WITH_ID;

	@Resource
	private TodoListRepository listRepo;

	@Resource
	private TodoListItemRepository itemRepo;

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

	private TodoListItemEntity _getItemEntity(final String itemId)
	{
		final TodoListItemEntity entity = itemRepo.findOne(Long.parseLong(itemId));
		if (entity == null) throw new IllegalArgumentException("Item with ID" + itemId + " not found");
		return entity;
	}

	private TodoListEntity _getListEntity(final String listId)
	{
		final TodoListEntity entity = listRepo.findOne(Long.parseLong(listId));
		if (entity == null) throw new IllegalArgumentException("List with ID" + listId + " not found");
		return entity;
	}

	@Transactional(readOnly = false)
	@Override
	public String createItem(TodoListItem item)
	{
		if (item == null) throw new IllegalArgumentException("[item] must not be null");
		if (item.getId() != null) throw new IllegalArgumentException("[item.id] must be null");
		if (item.getListId() == null) throw new IllegalArgumentException("[item.listId] must not be null");
		if (item.getDescription() == null) throw new IllegalArgumentException("[item.description] must not be null");

		// create a corresponding entity object and copy the attribute values
		TodoListItemEntity entity = MAPPER_WITHOUT_ID.map(item, TodoListItemEntity.class);
		entity.setList(_getListEntity(item.getListId()));
		entity = itemRepo.save(entity);
		return entity.getId().toString();
	}

	@Transactional(readOnly = false)
	@Override
	public String createList(TodoList list)
	{
		if (list == null) throw new IllegalArgumentException("[list] must not be null");
		if (list.getId() != null) throw new IllegalArgumentException("[list.id] must be null");
		if (list.getName() == null) throw new IllegalArgumentException("[list.name] must not be null");

		// create a corresponding entity object and copy the attribute values
		TodoListEntity entity = MAPPER_WITHOUT_ID.map(list, TodoListEntity.class);
		entity = listRepo.save(entity);
		return entity.getId().toString();
	}

	@Override
	public Collection<TodoListItem> findItems(String listId, String searchFor)
	{
		if (searchFor == null) return Collections.emptyList();
		searchFor = searchFor.trim();

		final List<TodoListItem> result = new ArrayList<>();
		for (final TodoListItemEntity entity : itemRepo.findByListAndDescriptionContainingIgnoreCase(_getListEntity(listId), searchFor))
			result.add(MAPPER_WITH_ID.map(entity, TodoListItem.class));
		return result;
	}

	@Override
	public Collection<TodoListItem> findItemsInAllLists(String searchFor)
	{
		if (searchFor == null) return Collections.emptyList();
		searchFor = searchFor.trim();

		final List<TodoListItem> result = new ArrayList<>();
		for (final TodoListItemEntity entity : itemRepo.findByDescriptionContainingIgnoreCase(searchFor))
			result.add(MAPPER_WITH_ID.map(entity, TodoListItem.class));
		return result;
	}

	@Override
	public Collection<TodoList> findLists(String searchFor)
	{
		if (searchFor == null) return Collections.emptyList();
		searchFor = searchFor.trim();

		final List<TodoList> result = new ArrayList<>();
		for (final TodoListEntity entity : listRepo.findByNameContainingOrDescriptionContainingAllIgnoreCase(searchFor, searchFor))
			result.add(MAPPER_WITH_ID.map(entity, TodoList.class));
		return result;

	}

	@Override
	public TodoListItem getItemById(String itemId)
	{
		if (itemId == null) throw new IllegalArgumentException("[itemId] must not be null");

		return MAPPER_WITH_ID.map(_getItemEntity(itemId), TodoListItem.class);
	}

	@Override
	public Collection<TodoListItem> getItems(String listId)
	{
		if (listId == null) throw new IllegalArgumentException("[listId] must not be null");

		final List<TodoListItem> result = new ArrayList<>();
		for (final TodoListItemEntity entity : _getListEntity(listId).getItems())
			result.add(MAPPER_WITH_ID.map(entity, TodoListItem.class));
		return result;
	}

	@Override
	public TodoList getListById(String listId)
	{
		if (listId == null) throw new IllegalArgumentException("[listId] must not be null");

		return MAPPER_WITH_ID.map(_getListEntity(listId), TodoList.class);
	}

	@Override
	public Collection<TodoList> getLists()
	{
		final List<TodoList> result = new ArrayList<>();
		for (final TodoListEntity entity : listRepo.findAll())
			result.add(MAPPER_WITH_ID.map(entity, TodoList.class));
		return result;
	}

	@Transactional(readOnly = false)
	@Override
	public void removeItem(String itemId)
	{
		if (itemId == null) throw new IllegalArgumentException("[itemId] must not be null");

		final TodoListItemEntity entity = _getItemEntity(itemId);
		final TodoListEntity owner = entity.getList();
		owner.getItems().remove(entity);
		listRepo.save(owner);
	}

	@Transactional(readOnly = false)
	@Override
	public void removeList(String listId)
	{
		if (listId == null) throw new IllegalArgumentException("[listId] must not be null");

		listRepo.delete(_getListEntity(listId));
	}

	@Transactional(readOnly = false)
	@Override
	public void updateItem(TodoListItem item)
	{
		if (item == null) throw new IllegalArgumentException("[item] must not be null");
		if (item.getId() == null) throw new IllegalArgumentException("[item.id] must not be null");
		if (item.getListId() == null) throw new IllegalArgumentException("[item.listId] must not be null");
		if (item.getDescription() == null) throw new IllegalArgumentException("[item.description] must not be null");

		final TodoListItemEntity entity = _getItemEntity(item.getId());
		MAPPER_WITHOUT_ID.map(item, entity);
		itemRepo.save(entity);
	}

	@Transactional(readOnly = false)
	@Override
	public void updateList(TodoList list)
	{
		if (list == null) throw new IllegalArgumentException("[list] must not be null");
		if (list.getId() == null) throw new IllegalArgumentException("[list.id] must not be null");
		if (list.getName() == null) throw new IllegalArgumentException("[list.name] must not be null");

		final TodoListEntity entity = _getListEntity(list.getId());
		MAPPER_WITHOUT_ID.map(list, entity);
		listRepo.save(entity);
	}
}
