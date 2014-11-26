package todo.ui.wicket;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.UrlRenderer;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.stereotype.Component;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 *
 * @author <a href="http://pl.ibm.com/">Marek Zajac</a>
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@Component("wicketApplication")
public class WicketApplication extends WebApplication
{
	@Override
	public Class< ? extends WebPage> getHomePage()
	{
		return ToDoListsPage.class;
	}

	@Override
	public void init()
	{
		super.init();
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		mountBookmarkablePages();
	}

	private void mountBookmarkablePages()
	{
		mountPage("/", ToDoListsPage.class);
		mountPage("/todolist/${todoListId}", ToDoListItemsPage.class);
	}

	/**
	 * WebSphere incorrectly handles relative redirect pages when "HttpServletResponse.sendRedirect(url)" is called.
	 *
	 * TODO This small fix ensures that WebSphere is only ever provided with absolute URLs so that this issue never occurs.
	 */
	@Override
	protected WebResponse newWebResponse(final WebRequest webRequest, final HttpServletResponse httpServletResponse)
	{
		return new ServletWebResponse((ServletWebRequest) webRequest, httpServletResponse)
			{
				@Override
				public String encodeRedirectURL(final CharSequence relativeURL)
				{
					return new UrlRenderer(webRequest).renderFullUrl(Url.parse(relativeURL));
				}
			};
	}
}
