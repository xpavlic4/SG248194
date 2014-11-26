package todo.service.jpa.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.Index;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@Entity
public class TodoListEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Basic(optional = false)
	@Column(nullable = false, length = 255)
	@Index(name = "IDX_NAME")
	private String name;

	@Basic
	@Column(nullable = true, length = 4096)
	@Index(name = "IDX_DESCR")
	private String description;

	@OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "list", orphanRemoval = true)
	@OrderColumn
	private final List<TodoListItemEntity> items;

	public TodoListEntity()
	{
		items = new ArrayList<>();
	}

	public String getDescription()
	{
		return description;
	}

	public Long getId()
	{
		return id;
	}

	public List<TodoListItemEntity> getItems()
	{
		return items;
	}

	public String getName()
	{
		return name;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setName(final String name)
	{
		this.name = name;
	}
}
