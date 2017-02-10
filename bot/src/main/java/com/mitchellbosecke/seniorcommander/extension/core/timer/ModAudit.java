package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class ModAudit implements Timer {

    private static final Logger logger = LoggerFactory.getLogger(ModAudit.class);

    private final long id;
    private final long interval;
    private final long channelId;
    private final SeniorCommander seniorCommander;
    private final UserService userService;

    public ModAudit(long id, long interval, long channelId, SeniorCommander seniorCommander, UserService userService) {
        this.id = id;
        this.interval = interval;
        this.channelId = channelId;
        this.seniorCommander = seniorCommander;
        this.userService = userService;
    }

    @Override
    public void perform() {
        Optional<Channel> optionalChannel = seniorCommander.getChannelManager().getChannel(channelId);
        if (optionalChannel.isPresent()) {
            TwitchChannel channel = (TwitchChannel) optionalChannel.get();
            logger.debug("Started mod audit.");

            //@formatter:off
            TransactionManager.getCurrentSession()
                    .createQuery("UPDATE CommunityUserModel " +
                            "SET accessLevel = 'USER' " +
                            "WHERE accessLevel = 'MODERATOR' " +
                            "AND communityModel = :communityModel")
                    .setParameter("communityModel", userService.findCommunity(channel)).executeUpdate();
            //@formatter:on

            channel.getModList();
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
