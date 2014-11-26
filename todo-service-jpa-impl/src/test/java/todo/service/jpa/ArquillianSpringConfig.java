package todo.service.jpa;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Java based Spring configuration.
 * Does the same as the XML-based configuration located in /src/test/resources/arquillian-spring-context.xml
 * @author <a href="http://vegardit.com/">Sebastian Thomschke</a>
 */
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "todo.service.jpa.repository")
@ComponentScan("todo.service.jpa")
@Configuration
public class ArquillianSpringConfig
{
	@Bean
	public DataSource dataSourceLookup()
	{
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		final DataSource dataSource = dsLookup.getDataSource("jdbc/TODOLIST");
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory()
	{
		final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSourceLookup());
		emf.setPackagesToScan("todo.service.jpa.model");
		emf.setJpaVendorAdapter(hibernateJpaVendorAdapter());
		return emf;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation()
	{
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public HibernateJpaVendorAdapter hibernateJpaVendorAdapter()
	{
		final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
		adapter.setGenerateDdl(true);
		adapter.setShowSql(true);
		return adapter;
	}

	@Bean
	public PlatformTransactionManager transactionManager()
	{
		final JpaTransactionManager txm = new JpaTransactionManager();
		txm.setEntityManagerFactory(entityManagerFactory().getObject());
		return txm;
	}
}
