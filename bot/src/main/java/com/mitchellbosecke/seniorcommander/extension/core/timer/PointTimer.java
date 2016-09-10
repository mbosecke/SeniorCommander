package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.timer.Timer;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class PointTimer implements Timer {

    private final long id;
    private final long interval;

    private final UserService userService;
    private final Channel channel;

    public PointTimer(long id, long interval, UserService userService, Channel channel) {
        this.id = id;
        this.interval = interval;
        this.userService = userService;
        this.channel = channel;
    }

    @Override
    public void perform() {
        userService.giveOnlineUsersPoints(channel, 1);
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
