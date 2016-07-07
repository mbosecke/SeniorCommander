package com.mitchellbosecke.seniorcommander.core;

import com.mitchellbosecke.seniorcommander.Channel;
import com.mitchellbosecke.seniorcommander.Extension;
import com.mitchellbosecke.seniorcommander.MessageHandler;
import com.mitchellbosecke.seniorcommander.core.channel.IrcChannel;
import com.mitchellbosecke.seniorcommander.core.handler.DiceHandler;
import com.mitchellbosecke.seniorcommander.core.handler.LoggingHandler;
import com.mitchellbosecke.seniorcommander.core.handler.OutputHandler;

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
        return messageHandlers;
    }

    @Override
    public List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<>();
        channels.add(new IrcChannel());
        return channels;
    }
}
