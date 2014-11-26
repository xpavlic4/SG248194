package todo.ui.rest;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;
import net.sourceforge.jwebunit.junit.WebTester;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;

public class RestApplicationITest extends TestCase
{
	private static final long DEFAULT_WAIT_TIME = 5000;
	private WebTester wt;

	private void waitForJavaScriptRendering(long waitTime)
	{
		try
		{
			Thread.sleep(waitTime);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Before
	public void setUp()
	{
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 9080;
		RestAssured.basePath = "/api";
		
		wt = new WebTester();
		wt.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		wt.setBaseUrl("http://localhost:9080");
	}
	
	@After
	public void tearDown()
	{
		// Delete the test list if it exists (don't bother validating)
		expect().then().
			delete("/lists/1");
	}

	@Test
	public void testHomePage()
	{
		wt.beginAt("index.html");
		waitForJavaScriptRendering(DEFAULT_WAIT_TIME);
		wt.assertTitleEquals("Todo Web Application");
	}
	
	@Test
	public void testAddList() 
	{
		// Check that no lists exist already
		expect().
			statusCode(200).
			body("size()", is(0)).
		when().
			get("/lists");
		
		// Add a test list
		given().
			contentType(MediaType.APPLICATION_JSON).
			body("{\"name\": \"test list name\", \"description\": \"test list description\"}").
		then().expect().
			statusCode(200).
			body(instanceOf(String.class)).
		when().
			post("/lists");
		
		// Check that the new list was added
		expect().
			statusCode(200).
			body("size()", is(1)).
		when().
			get("/lists");
		
		// Check the Dojo UI displays the new list
		wt.beginAt("index.html");
		waitForJavaScriptRendering(DEFAULT_WAIT_TIME);
		wt.assertTextPresent("test list name - test list description");
	}
}
