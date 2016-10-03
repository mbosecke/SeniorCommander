package com.mitchellbosecke.seniorcommander.extension.core;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.channel.SocketChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.command.*;
import com.mitchellbosecke.seniorcommander.extension.core.event.*;
import com.mitchellbosecke.seniorcommander.extension.core.service.*;
import com.mitchellbosecke.seniorcommander.extension.core.timer.*;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class CoreExtension implements Extension {

    private static final Logger logger = LoggerFactory.getLogger(CoreExtension.class);

    @Override
    public List<ChannelFactory> getChannelFactories() {
        List<ChannelFactory> factories = new ArrayList<>();
        factories.add(new TwitchChannelFactory());
        factories.add(new SocketChannelFactory());
        return factories;
    }

    @Override
    public void startTimers(SessionFactory sessionFactory, MessageQueue messageQueue, List<Channel> channels,
                            TimerManager timerManager) {

        UserService userService = new UserService(sessionFactory);

        new ShoutTimerFactory(sessionFactory, channels, messageQueue).build().forEach(timerManager::addTimer);
        new PointTimerFactory(sessionFactory, channels, userService).build().forEach(timerManager::addTimer);
        new TwitchOnlineCheckerFactory(sessionFactory, channels).build().forEach(timerManager::addTimer);
        new FollowerAuditFactory(sessionFactory, channels, userService).build().forEach(timerManager::addTimer);
        new ModAuditFactory(sessionFactory, channels, userService).build().forEach(timerManager::addTimer);
    }

    @Override
    public List<EventHandler> buildEventHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                                 List<Channel> channels, List<CommandHandler> commandHandlers) {

        List<EventHandler> eventHandlers = new ArrayList<>();

        // service tiers
        UserService userService = new UserService(sessionFactory);
        CommandService commandService = new CommandService(sessionFactory);

        // handlers
        eventHandlers.add(new LoggingHandler(userService));
        eventHandlers.add(new OutputHandler(channels));
        eventHandlers.add(new ConversationalHandler(messageQueue));
        eventHandlers.add(new UserChatHandler(userService));
        eventHandlers.add(new JoinPartHandler(userService));
        eventHandlers.add(new NamesHandler(userService));
        eventHandlers.add(new CommandBroker(messageQueue, commandHandlers, userService, commandService));
        eventHandlers.add(new ModListHandler(userService));

        return eventHandlers;
    }

    @Override
    public List<CommandHandler> buildCommandHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                                     TimerManager timerManager) {

        // service tiers
        CommandService commandService = new CommandService(sessionFactory);
        QuoteService quoteService = new QuoteService(sessionFactory);
        TimerService timerService = new TimerService(sessionFactory);
        UserService userService = new UserService(sessionFactory);
        BettingService bettingService = new BettingService(sessionFactory);

        // handlers
        List<CommandHandler> commandHandlers = new ArrayList<>();
        commandHandlers.add(new Roll(messageQueue));
        commandHandlers.add(new Advice(messageQueue));
        commandHandlers.add(new Roulette(messageQueue));
        commandHandlers.add(new CommandCrud(messageQueue, commandService));
        commandHandlers.add(new QuoteCrud(messageQueue, quoteService));
        commandHandlers.add(new RandomQuote(messageQueue, quoteService));
        commandHandlers.add(new TimerCrud(messageQueue, timerService, timerManager, userService));
        commandHandlers.add(new Betting(messageQueue, bettingService, userService));
        commandHandlers.add(new Points(messageQueue, userService));
        return commandHandlers;
    }

    @Override
    public void onShutdown(SessionFactory sessionFactory) {
        // do nothing
        try {
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();

            int result = session.createNativeQuery("DELETE FROM core.online_channel_user").executeUpdate();
            logger.debug("Deleted " + result + " records from online_channel_user");
            session.getTransaction().commit();
        }catch(Exception ex){
            logger.debug("Rolling back the deletion from online_channel_user");
            sessionFactory.getCurrentSession().getTransaction().rollback();
            throw ex;
        }
    }
}
