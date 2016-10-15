package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class ModAudit implements Timer {

    private static final Logger logger = LoggerFactory.getLogger(ModAudit.class);

    private final long id;
    private final long interval;
    private final TwitchChannel channel;
    private final UserService userService;
    private final SessionFactory sessionFactory;

    public ModAudit(long id, long interval, TwitchChannel channel, UserService userService,
                    SessionFactory sessionFactory) {
        this.id = id;
        this.interval = interval;
        this.channel = channel;
        this.userService = userService;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void perform() {
        logger.debug("Started mod audit.");

        sessionFactory.getCurrentSession()
                .createQuery("UPDATE CommunityUserModel SET accessLevel = 'USER' WHERE accessLevel = 'MODERATOR' AND communityModel = :communityModel")
                .setParameter("communityModel", userService.findCommunity(channel)).executeUpdate();

        channel.getModList();
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
