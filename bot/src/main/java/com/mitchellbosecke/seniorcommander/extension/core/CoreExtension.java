package com.mitchellbosecke.seniorcommander.extension.core;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.extension.core.command.AdviceCommand;
import com.mitchellbosecke.seniorcommander.extension.core.command.RollCommand;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.channel.IrcChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.SocketChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.event.*;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommunityService;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommunityServiceImpl;
import com.mitchellbosecke.seniorcommander.EventHandler;
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
    public List<EventHandler> buildEventHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                                 List<Channel> channels, List<CommandHandler> commandHandlers) {

        List<EventHandler> eventHandlers = new ArrayList<>();

        CommunityService communityService = new CommunityServiceImpl(sessionFactory);

        eventHandlers.add(new LoggingHandler());
        eventHandlers.add(new OutputHandler(channels));
        eventHandlers.add(new ConversationalHandler(messageQueue));
        eventHandlers.add(new UserChatHandler(communityService));
        eventHandlers.add(new JoinPartHandler(communityService));
        eventHandlers.add(new NamesHandler(communityService));

        eventHandlers.add(new CommandBroker(communityService, messageQueue, commandHandlers));

        eventHandlers.add(new RouletteHandler(messageQueue));
        eventHandlers.add(new CommandCrudHandler(communityService, messageQueue));
        return eventHandlers;
    }

    @Override
    public List<ChannelFactory> getChannelFactories() {
        List<ChannelFactory> factories = new ArrayList<>();
        factories.add(new IrcChannelFactory());
        factories.add(new SocketChannelFactory());
        return factories;
    }

    @Override
    public TimerFactory getTimerFactory() {
        return new CoreTimerFactory();
    }

    @Override
    public List<CommandHandler> buildCommandHandlers(SessionFactory sessionFactory, MessageQueue messageQueue) {
        List<CommandHandler> commandHandlers = new ArrayList<>();
        commandHandlers.add(new RollCommand(messageQueue));
        commandHandlers.add(new AdviceCommand(messageQueue));
        return commandHandlers;
    }
}
