package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Properties;

/**
 * Created by mitch_000 on 2016-07-09.
 */
public class DatabaseManager {

    private static final String CONFIG_URL = "database.url";
    private static final String CONFIG_USERNAME = "database.username";
    private static final String CONFIG_PASSWORD = "database.password";
    private static final String CONFIG_SCHEMA = "database.schema";

    private final Configuration configuration;

    public DatabaseManager(Configuration configuration) {
        this.configuration = configuration;
    }

    public SessionFactory getSessionFactory() {
        SessionFactory sessionFactory = null;

        Properties config = new Properties();
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        config.setProperty("hibernate.connection.url", configuration.getProperty(CONFIG_URL));
        config.setProperty("hibernate.connection.username", configuration.getProperty(CONFIG_USERNAME));
        config.setProperty("hibernate.connection.password", configuration.getProperty(CONFIG_PASSWORD));

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config).build();
        try {
            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(Community.class);
            sources.addAnnotatedClass(CommunityUser.class);
            MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
            metadataBuilder.applyImplicitSchemaName("core");

            sessionFactory = metadataBuilder.build().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return sessionFactory;
    }

    public void migrate() {
        Flyway flyway = new Flyway();

        String url = configuration.getProperty(CONFIG_URL);
        String username = configuration.getProperty(CONFIG_USERNAME);
        String password = configuration.getProperty(CONFIG_PASSWORD);
        flyway.setDataSource(url, username, password);
        flyway.setSchemas(configuration.getProperty(CONFIG_SCHEMA));

        flyway.migrate();
    }
}
