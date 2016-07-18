package com.mitchellbosecke.seniorcommander.utils;

import com.mitchellbosecke.seniorcommander.domain.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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

    private static final String CONFIG_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String CONFIG_HIBERNATE_DRIVER_CLASS = "hibernate.connection.driver_class";

    private Flyway flyway;

    public DatabaseManager() {
        flyway = new Flyway();
        Config configuration = ConfigFactory.load();
        String url = configuration.getString(CONFIG_URL);
        String username = configuration.getString(CONFIG_USERNAME);
        String password = configuration.getString(CONFIG_PASSWORD);
        flyway.setDataSource(url, username, password);
        flyway.setSchemas(configuration.getString(CONFIG_SCHEMA));
    }

    public SessionFactory getSessionFactory() {
        SessionFactory sessionFactory;

        Config configuration = ConfigFactory.load();

        Properties config = new Properties();
        config.setProperty("hibernate.dialect", configuration.getString(CONFIG_HIBERNATE_DIALECT));
        config.setProperty("hibernate.connection.driver_class", configuration.getString(CONFIG_HIBERNATE_DRIVER_CLASS));
        config.setProperty("hibernate.connection.url", configuration.getString(CONFIG_URL));
        config.setProperty("hibernate.connection.username", configuration.getString(CONFIG_USERNAME));
        config.setProperty("hibernate.connection.password", configuration.getString(CONFIG_PASSWORD));
        config.setProperty("hibernate.current_session_context_class", "org.hibernate.context.internal.ThreadLocalSessionContext");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config).build();
        try {
            MetadataSources sources = new MetadataSources(registry);

            // register all persistent entities
            sources.addAnnotatedClass(Community.class);
            sources.addAnnotatedClass(CommunityUser.class);
            sources.addAnnotatedClass(ChannelConfiguration.class);
            sources.addAnnotatedClass(ChannelConfigurationSetting.class);
            sources.addAnnotatedClass(Command.class);
            sources.addAnnotatedClass(CommandLog.class);

            MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
            metadataBuilder.applyImplicitSchemaName(configuration.getString(CONFIG_SCHEMA));

            sessionFactory = metadataBuilder.build().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new RuntimeException(e);
        }
        return sessionFactory;
    }

    public void migrate() {
        flyway.migrate();
    }

    public void teardown() {
        flyway.clean();
    }
}
