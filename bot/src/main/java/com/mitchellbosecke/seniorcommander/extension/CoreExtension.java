package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.channel.IrcChannel;
import com.mitchellbosecke.seniorcommander.handler.DiceHandler;
import com.mitchellbosecke.seniorcommander.handler.LoggingHandler;
import com.mitchellbosecke.seniorcommander.handler.OutputHandler;
import com.mitchellbosecke.seniorcommander.scheduled.ScheduledTask;
import com.mitchellbosecke.seniorcommander.scheduled.TimedShout;

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

    @Override
    public List<ScheduledTask> getScheduledTasks() {
        List<ScheduledTask> scheduledTask = new ArrayList<>();
        scheduledTask.add(new TimedShout("To be a good commander, you must be willing to order the death of the thing" +
                " you love", 60));
        return scheduledTask;
    }
}
