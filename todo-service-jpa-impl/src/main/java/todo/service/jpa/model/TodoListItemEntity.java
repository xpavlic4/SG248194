package todo.service.jpa.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Index;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@Entity
public class TodoListItemEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false)
	private TodoListEntity list;

	@Basic(optional = false)
	@Column(nullable = false, length = 4096)
	@Index(name = "IDX_DESCR")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;

	@Basic
	private boolean isDone;

	public String getDescription()
	{
		return description;
	}

	public Date getDueDate()
	{
		return dueDate;
	}

	public Long getId()
	{
		return id;
	}

	public TodoListEntity getList()
	{
		return list;
	}

	public boolean isDone()
	{
		return isDone;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setDone(boolean isDone)
	{
		this.isDone = isDone;
	}

	public void setDueDate(Date dueDate)
	{
		this.dueDate = dueDate;
	}

	public void setList(TodoListEntity list)
	{
		if (this.list == list) return;

		if (this.list != null) this.list.getItems().remove(this);

		this.list = list;
		if (list.getItems().contains(this)) return;
		list.getItems().add(this);
	}
}
