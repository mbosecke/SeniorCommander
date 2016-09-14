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
import com.mitchellbosecke.seniorcommander.extension.core.service.CommandService;
import com.mitchellbosecke.seniorcommander.extension.core.service.QuoteService;
import com.mitchellbosecke.seniorcommander.extension.core.service.TimerService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.extension.core.timer.FollowerTrackerFactory;
import com.mitchellbosecke.seniorcommander.extension.core.timer.PointTimerFactory;
import com.mitchellbosecke.seniorcommander.extension.core.timer.ShoutTimerFactory;
import com.mitchellbosecke.seniorcommander.extension.core.timer.TwitchOnlineCheckerFactory;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class CoreExtension implements Extension {

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

        new ShoutTimerFactory().build(sessionFactory.getCurrentSession(), channels, messageQueue)
                .forEach(timerManager::addTimer);
        new PointTimerFactory().build(sessionFactory.getCurrentSession(), channels, userService)
                .forEach(timerManager::addTimer);
        new TwitchOnlineCheckerFactory().build(sessionFactory.getCurrentSession(), channels, userService)
                .forEach(timerManager::addTimer);
        new FollowerTrackerFactory().build(sessionFactory, channels, userService).forEach(timerManager::addTimer);
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

        return eventHandlers;
    }

    @Override
    public List<CommandHandler> buildCommandHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                                     TimerManager timerManager) {

        // service tiers
        CommandService commandService = new CommandService(sessionFactory);
        QuoteService quoteService = new QuoteService(sessionFactory);
        TimerService timerService = new TimerService(sessionFactory);

        // handlers
        List<CommandHandler> commandHandlers = new ArrayList<>();
        commandHandlers.add(new Roll(messageQueue));
        commandHandlers.add(new Advice(messageQueue));
        commandHandlers.add(new Roulette(messageQueue));
        commandHandlers.add(new CommandCrud(messageQueue, commandService));
        commandHandlers.add(new QuoteCrud(messageQueue, quoteService));
        commandHandlers.add(new RandomQuote(messageQueue, quoteService));
        commandHandlers.add(new TimerCrud(messageQueue, timerService, timerManager));
        return commandHandlers;
    }

}
