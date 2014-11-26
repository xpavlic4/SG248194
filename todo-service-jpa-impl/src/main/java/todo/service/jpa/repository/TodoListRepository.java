package todo.service.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import todo.service.jpa.model.TodoListEntity;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public interface TodoListRepository extends JpaRepository<TodoListEntity, Long>
{
	List<TodoListEntity> findByNameContainingOrDescriptionContainingAllIgnoreCase(String searchFor1, String searchFor2);

}
