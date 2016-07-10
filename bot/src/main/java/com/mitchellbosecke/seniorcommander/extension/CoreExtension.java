package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.channel.IrcChannelFactory;
import com.mitchellbosecke.seniorcommander.channel.SocketChannelFactory;
import com.mitchellbosecke.seniorcommander.handler.*;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.timer.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class CoreExtension implements Extension {

    @Override
    public List<MessageHandler> getMessageHandlers() {
        List<MessageHandler> messageHandlers = new ArrayList<>();
        messageHandlers.add(new LoggingHandler());
        messageHandlers.add(new DiceHandler());
        messageHandlers.add(new OutputHandler());
        messageHandlers.add(new CommandsHandler());
        messageHandlers.add(new ConversationalHandler());
        messageHandlers.add(new UserAddHandler());
        messageHandlers.add(new RouletteHandler());
        messageHandlers.add(new AdviceHandler());
        return messageHandlers;
    }

    @Override
    public List<ChannelFactory> getChannelFactories() {
        List<ChannelFactory> factories = new ArrayList<>();
        factories.add(new IrcChannelFactory());
        factories.add(new SocketChannelFactory());
        return factories;
    }

    @Override
    public List<Timer> getTimers() {
        List<Timer> timer = new ArrayList<>();
        return timer;
    }
}
