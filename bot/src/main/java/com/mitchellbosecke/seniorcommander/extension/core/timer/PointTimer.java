package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class PointTimer implements Timer {

    private static final Logger logger = LoggerFactory.getLogger(PointTimer.class);

    private static final int DEFAULT_POINT_ONLINE = 1;
    private static final String SETTING_POINTS_ONLINE = "points.online";

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

        CommunityModel community = userService.findCommunity(channel);

        String pointSetting = null;
        int defaultPoints = 0;


        if (channel.isOnline()) {
            logger.debug("Channel is online. [" + channel.getClass().getSimpleName() + "]");

            pointSetting = community.getSetting(SETTING_POINTS_ONLINE);
            defaultPoints = DEFAULT_POINT_ONLINE;

            int points = pointSetting == null? defaultPoints : Integer.valueOf(pointSetting);
            userService.giveOnlineUsersPoints(channel, points);
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
