package todo.ui.wicket;

import junit.framework.TestCase;
import net.sourceforge.jwebunit.junit.WebTester;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
public class WicketApplicationITest extends TestCase
{
	public void testWicketApplication()
	{
		final WebTester tester = new WebTester();
		tester.setBaseUrl("http://localhost:9080");
		tester.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		tester.beginAt("/");
		tester.assertTitleEquals("Todo List Application");

		tester.assertTextPresent("You currently have");
		tester.assertFormPresent("new_todo_list_form1");

		// create two new todo lists
		tester.setTextField("name", "List1_Name");
		tester.setTextField("description", "List1_Description");
		tester.submit("submitButton");
		tester.setTextField("name", "List2_Name");
		tester.setTextField("description", "List2_Description");
		tester.submit("submitButton");

		tester.assertTextPresent("List1_Name");
		tester.assertTextPresent("List2_Name");
		tester.assertTextPresent("List1_Description");
		tester.assertTextPresent("List2_Description");

		// goto list detail page
		tester.clickElementByXPath("//div[@class='list-group-item']/h4[contains(.,'List1_Name')]/a[contains(.,'View')]");

		// create two items
		tester.setTextField("description", "Item1");
		tester.submit("submitButton");

		tester.setTextField("description", "Item2");
		tester.submit("submitButton");

		tester.assertTextPresent("Item1");
		tester.assertTextPresent("Item2");
	}
}
