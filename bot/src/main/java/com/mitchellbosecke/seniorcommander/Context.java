package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.scheduled.ScheduledTask;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class Context {

    private final Configuration configuration;

    private final MessageQueue messageQueue;

    private final List<Channel> channels;

    private final List<MessageHandler> handlers;

    private final List<ScheduledTask> scheduledTasks;

    private final ScheduledExecutorService scheduledExecutorService;

    public Context(Configuration configuration, MessageQueue messageQueue, List<Channel> channels,
                   List<MessageHandler> handlers, List<ScheduledTask> scheduledTasks,
                   ScheduledExecutorService scheduledExecutorService) {
        this.configuration = configuration;
        this.messageQueue = messageQueue;
        this.channels = channels;
        this.handlers = handlers;
        this.scheduledTasks = scheduledTasks;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<MessageHandler> getMessageHandlers() {
        return handlers;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public List<MessageHandler> getHandlers() {
        return handlers;
    }

    public List<ScheduledTask> getScheduledTasks() {
        return scheduledTasks;
    }
}
