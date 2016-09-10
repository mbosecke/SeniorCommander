package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.Timer;

/**
 * Created by mitch_000 on 2016-07-31.
 */
public class Shout implements Timer {

    private final long id;

    private final long interval;

    private final MessageQueue messageQueue;

    private final Channel channel;

    private final String message;

    public Shout(long id, long interval, MessageQueue messageQueue, Channel channel, String message) {
        this.id = id;
        this.interval = interval;
        this.messageQueue = messageQueue;
        this.message = message;
        this.channel = channel;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    @Override
    public void perform() {
        messageQueue.add(Message.shout(channel, message));
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getInterval() {
        return interval;
    }
}
