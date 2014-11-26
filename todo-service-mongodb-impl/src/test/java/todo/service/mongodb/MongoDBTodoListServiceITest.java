package todo.service.mongodb;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import todo.service.AbstractTodoListServiceTest;
import todo.service.mongodb.MongoDBTodoListService;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@RunWith(Arquillian.class)
public class MongoDBTodoListServiceITest extends AbstractTodoListServiceTest
{
	@Deployment
	public static Archive< ? > createDeployment()
	{
		final File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importDependencies(ScopeType.COMPILE, ScopeType.TEST).resolve()
				.withTransitivity().asFile();

		return ShrinkWrap.create(WebArchive.class) //
				.addClass(MongoDBTodoListService.class) //
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml") // flag archive as bean archive to enable CDI i.e. @Inject https://docs.jboss.org/author/display/ARQ/Dependency+injection
				.addAsLibraries(libs) //
				.setWebXML(new StringAsset("" //
						+ "<?xml version='1.0' encoding='UTF-8'?>\n" //
						+ "<web-app version='3.0' xmlns='http://java.sun.com/xml/ns/javaee'>\n" //
						+ "   <resource-env-ref>\n" //
						+ "      <resource-env-ref-name>" + MongoDBTodoListService.MONGO_DB_JNDI_NAME + "</resource-env-ref-name>\n" //
						+ "      <resource-env-ref-type>com.mongodb.DB</resource-env-ref-type>\n" //
						+ "   </resource-env-ref>\n" //
						+ "</web-app>\n" //
				));
	}

	@Inject
	private MongoDBTodoListService todoListService;

	private MongodExecutable mongodExe;
	private MongodProcess mongod;

	@Before
	public void setupMongoDB() throws UnknownHostException, IOException
	{
		// this triggers downloading of the platform specific MongoDB executables
		final MongodStarter runtime = MongodStarter.getDefaultInstance();

		final IMongodConfig mongodConfig = new MongodConfigBuilder() //
				.version(Version.Main.PRODUCTION)//
				.net(new Net("localhost", 9991, Network.localhostIsIPv6())) //
				.build();

		mongodExe = runtime.prepare(mongodConfig);
		mongod = mongodExe.start();
	}

	@After
	public void tearDownEmbeddedMongoDB()
	{
		if (mongod != null)
		{
			mongod.stop();
			mongodExe.stop();
		}
	}

	@Test
	public void testMongoDBTodoListService()
	{
		testImplementation(todoListService);
	}
}
