package todo.service.jpa;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import todo.service.AbstractTodoListServiceTest;
import todo.service.TodoListService;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@RunWith(Arquillian.class)
public class HibernateJPATodoListServiceITest extends AbstractTodoListServiceTest
{
	/**
	 * If set to true XML based Spring configuration is used.
	 * If set to false Java based Spring configuration is used.
	 */
	private static final boolean USE_XML_BASED_SPRING_CONFIG = false;
	private static final String SPRING_CONFIG_FILE = "arquillian-spring-config.xml";

	@Deployment
	public static Archive< ? > createDeployment()
	{
		final File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importDependencies(ScopeType.COMPILE, ScopeType.TEST).resolve()
				.withTransitivity().asFile();

		final WebArchive archive = ShrinkWrap.create(WebArchive.class) //
				.addPackages(true, todo.service.jpa.JPATodoListService.class.getPackage()) //
				.addAsLibraries(libs) //
				.setWebXML(new StringAsset("" //
						+ "<?xml version='1.0' encoding='UTF-8'?>\n" //
						+ "<web-app version='3.0' xmlns='http://java.sun.com/xml/ns/javaee'>\n" //
						+ "   <resource-env-ref>\n" //
						+ "      <resource-env-ref-name>jdbc/TODOLIST</resource-env-ref-name>\n" //
						+ "      <resource-env-ref-type>javax.sql.DataSource</resource-env-ref-type>\n" //
						+ "   </resource-env-ref>\n" //
						+ "</web-app>\n" //
				));
		if (USE_XML_BASED_SPRING_CONFIG)
			archive.addAsWebInfResource(new File("src/test/resources/" + SPRING_CONFIG_FILE), "classes/" + SPRING_CONFIG_FILE);
		return archive;
	}

	private TodoListService todoListService;

	@Before
	public void setupSpring()
	{
		final ApplicationContext ctx;
		if (USE_XML_BASED_SPRING_CONFIG)
			ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE);
		else
			ctx = new AnnotationConfigApplicationContext(ArquillianSpringConfig.class);
		todoListService = ctx.getBean(TodoListService.class);
	}

	@Test
	public void testJPATodoListService()
	{
		testImplementation(todoListService);
	}
}
