package todo.ui.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import todo.service.TodoListService;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public abstract class AbstractBasePage extends WebPage
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	protected TodoListService backend;

	protected AbstractBasePage()
	{
		super();
	}

	protected AbstractBasePage(final IModel< ? > model)
	{
		super(model);
	}

	protected AbstractBasePage(final PageParameters parameters)
	{
		super(parameters);
	}
}
