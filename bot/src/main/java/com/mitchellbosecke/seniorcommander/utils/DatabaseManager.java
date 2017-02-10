package com.mitchellbosecke.seniorcommander.utils;

import com.mitchellbosecke.seniorcommander.domain.*;
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
    private static final String CONFIG_HIBERNATE_JDBC_TIME_ZONE = "hibernate.jdbc.time_zone";

    private Flyway flyway;

    public DatabaseManager() {
        flyway = new Flyway();
        String url = ConfigUtils.getString(CONFIG_URL);
        String username = ConfigUtils.getString(CONFIG_USERNAME);
        String password = ConfigUtils.getString(CONFIG_PASSWORD);
        flyway.setDataSource(url, username, password);
        flyway.setSchemas(ConfigUtils.getString(CONFIG_SCHEMA));
    }

    public SessionFactory getSessionFactory() {
        SessionFactory sessionFactory;

        Properties config = new Properties();
        config.setProperty("hibernate.dialect", ConfigUtils.getString(CONFIG_HIBERNATE_DIALECT));
        config.setProperty("hibernate.connection.driver_class", ConfigUtils.getString(CONFIG_HIBERNATE_DRIVER_CLASS));
        config.setProperty("hibernate.connection.url", ConfigUtils.getString(CONFIG_URL));
        config.setProperty("hibernate.connection.username", ConfigUtils.getString(CONFIG_USERNAME));
        config.setProperty("hibernate.connection.password", ConfigUtils.getString(CONFIG_PASSWORD));
        config.setProperty("hibernate.current_session_context_class", "org.hibernate.context.internal.ThreadLocalSessionContext");
        config.setProperty("hibernate.jdbc.time_zone", ConfigUtils.getString(CONFIG_HIBERNATE_JDBC_TIME_ZONE));

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config).build();
        try {
            MetadataSources sources = new MetadataSources(registry);

            // register all persistent entities
            sources.addAnnotatedClass(CommunityModel.class);
            sources.addAnnotatedClass(CommunitySettingModel.class);
            sources.addAnnotatedClass(CommunityUserModel.class);
            sources.addAnnotatedClass(ChannelModel.class);
            sources.addAnnotatedClass(ChannelSettingModel.class);
            sources.addAnnotatedClass(CommandModel.class);
            sources.addAnnotatedClass(CommandLogModel.class);
            sources.addAnnotatedClass(QuoteModel.class);
            sources.addAnnotatedClass(TimerModel.class);
            sources.addAnnotatedClass(ChatLogModel.class);
            sources.addAnnotatedClass(BettingGameModel.class);
            sources.addAnnotatedClass(BettingOptionModel.class);
            sources.addAnnotatedClass(BetModel.class);
            sources.addAnnotatedClass(GiveawayModel.class);
            sources.addAnnotatedClass(GiveawayEntryModel.class);
            sources.addAnnotatedClass(AuctionModel.class);

            MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
            metadataBuilder.applyImplicitSchemaName(ConfigUtils.getString(CONFIG_SCHEMA));

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
