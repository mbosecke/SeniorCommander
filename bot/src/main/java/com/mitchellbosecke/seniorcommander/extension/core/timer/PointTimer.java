package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class PointTimer implements Timer {

    private static final Logger logger = LoggerFactory.getLogger(PointTimer.class);

    private final long id;
    private final long interval;

    private final UserService userService;
    private final Channel channel;

    public PointTimer(long id, long interval, Channel channel, UserService userService) {
        this.id = id;
        this.interval = interval;
        this.userService = userService;
        this.channel = channel;
    }

    @Override
    public void perform() {

        if (channel.isOnline()) {
            logger.debug("Channel is online. [" + channel.getClass().getSimpleName() + "]");
            userService.giveOnlineUsersPoints(channel, 10);
        } else {
            logger.debug("Channel is offline. [" + channel.getClass().getSimpleName() + "]");
            userService.giveOnlineUsersPoints(channel, 1);
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
