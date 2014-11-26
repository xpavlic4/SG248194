package todo.service.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import todo.service.jpa.model.TodoListEntity;
import todo.service.jpa.model.TodoListItemEntity;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public interface TodoListItemRepository extends JpaRepository<TodoListItemEntity, Long>
{
	List<TodoListItemEntity> findByDescriptionContainingIgnoreCase(String searchFor);

	List<TodoListItemEntity> findByListAndDescriptionContainingIgnoreCase(TodoListEntity todoList, String searchFor);
}
