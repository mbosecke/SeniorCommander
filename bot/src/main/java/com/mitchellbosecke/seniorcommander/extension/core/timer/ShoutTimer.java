package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-31.
 */
public class ShoutTimer implements Timer {

    private static final Logger logger = LoggerFactory.getLogger(ShoutTimer.class);

    private final long id;

    private final long interval;

    private final MessageQueue messageQueue;

    private final Channel channel;

    private final String message;

    public ShoutTimer(long id, long interval, Channel channel,MessageQueue messageQueue, String message) {
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
        logger.debug("Performing ShoutTimer");
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
