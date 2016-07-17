package com.mitchellbosecke.seniorcommander.extension.core;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.channel.IrcChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.SocketChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.command.AdviceCommand;
import com.mitchellbosecke.seniorcommander.extension.core.command.CommandCrudCommand;
import com.mitchellbosecke.seniorcommander.extension.core.command.RollCommand;
import com.mitchellbosecke.seniorcommander.extension.core.command.RouletteCommand;
import com.mitchellbosecke.seniorcommander.extension.core.event.*;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommandService;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommandServiceImpl;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserServiceImpl;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerFactory;
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
        factories.add(new IrcChannelFactory());
        factories.add(new SocketChannelFactory());
        return factories;
    }

    @Override
    public List<EventHandler> buildEventHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                                 List<Channel> channels, List<CommandHandler> commandHandlers) {

        List<EventHandler> eventHandlers = new ArrayList<>();

        // service tiers
        UserService userService = new UserServiceImpl(sessionFactory);
        CommandService commandService = new CommandServiceImpl(sessionFactory);

        // handlers
        eventHandlers.add(new LoggingHandler());
        eventHandlers.add(new OutputHandler(channels));
        eventHandlers.add(new ConversationalHandler(messageQueue));
        eventHandlers.add(new UserChatHandler(userService));
        eventHandlers.add(new JoinPartHandler(userService));
        eventHandlers.add(new NamesHandler(userService));
        eventHandlers.add(new CommandBroker(messageQueue, commandHandlers, userService, commandService));

        return eventHandlers;
    }

    @Override
    public List<CommandHandler> buildCommandHandlers(SessionFactory sessionFactory, MessageQueue messageQueue) {

        // service tiers
        UserService userService = new UserServiceImpl(sessionFactory);
        CommandService commandService = new CommandServiceImpl(sessionFactory);

        // handlers
        List<CommandHandler> commandHandlers = new ArrayList<>();
        commandHandlers.add(new RollCommand(messageQueue));
        commandHandlers.add(new AdviceCommand(messageQueue));
        commandHandlers.add(new RouletteCommand(messageQueue));
        commandHandlers.add(new CommandCrudCommand(messageQueue, userService, commandService));
        return commandHandlers;
    }

    @Override
    public TimerFactory getTimerFactory() {
        return new CoreTimerFactory();
    }
}
