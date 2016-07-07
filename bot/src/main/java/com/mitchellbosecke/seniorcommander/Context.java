package com.mitchellbosecke.seniorcommander;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class Context {

    private final Configuration configuration;

    private final MessageQueue messageQueue;

    private final List<Channel> channels;

    private final List<MessageHandler> handlers;

    public Context(Configuration configuration, MessageQueue messageQueue, List<Channel> channels, List<MessageHandler> handlers) {
        this.configuration = configuration;
        this.messageQueue = messageQueue;
        this.channels = channels;
        this.handlers = handlers;
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
}
