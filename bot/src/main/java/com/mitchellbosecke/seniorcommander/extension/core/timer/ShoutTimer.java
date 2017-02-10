package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by mitch_000 on 2016-07-31.
 */
public class ShoutTimer implements Timer {

    private static final Logger logger = LoggerFactory.getLogger(ShoutTimer.class);

    private final long id;

    private final long interval;

    private final long channelId;

    private final SeniorCommander seniorCommander;

    private final String message;

    public ShoutTimer(long id, long interval, long channelId, SeniorCommander seniorCommander, String message) {
        this.id = id;
        this.interval = interval;
        this.seniorCommander = seniorCommander;
        this.message = message;
        this.channelId = channelId;
    }

    @Override
    public void perform() {
        Optional<Channel> channel = seniorCommander.getChannelManager().getChannel(channelId);

        if (channel.isPresent()) {
            logger.debug("Performing ShoutTimer");

            seniorCommander.getMessageQueue().add(Message.shout(channel.get(), message));
        }
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
