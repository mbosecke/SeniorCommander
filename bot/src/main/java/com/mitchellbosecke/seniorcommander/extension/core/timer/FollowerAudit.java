package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.twitchapi.ChannelFollow;
import com.mitchellbosecke.twitchapi.ChannelFollowsPage;
import com.mitchellbosecke.twitchapi.TwitchApi;
import com.typesafe.config.ConfigFactory;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class FollowerAudit implements Timer {

    private static final Logger logger = LoggerFactory.getLogger(FollowerAudit.class);

    private final long id;
    private final long interval;
    private final TwitchChannel channel;
    private final UserService userService;
    private final SessionFactory sessionFactory;

    public FollowerAudit(long id, long interval, TwitchChannel channel, UserService userService,
                         SessionFactory sessionFactory) {
        this.id = id;
        this.interval = interval;
        this.channel = channel;
        this.userService = userService;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void perform() {
        logger.debug("Started follow audit.");

        String twitchClientId = ConfigFactory.load().getConfig("seniorcommander").getString("twitch.clientId");
        TwitchApi twitchApi = new TwitchApi(twitchClientId);

        sessionFactory.getCurrentSession()
                .createQuery("UPDATE CommunityUserModel SET accessLevel = 'USER' WHERE accessLevel = 'FOLLOWER' AND communityModel = :communityModel")
                .setParameter("communityModel", userService.findCommunity(channel)).executeUpdate();

        ChannelFollowsPage page = twitchApi.followers(channel.getChannel());

        int count = 0;
        while (page != null && !page.getFollows().isEmpty()) {
            for (ChannelFollow follow : page.getFollows()) {
                CommunityUserModel user = userService.findUser(channel, follow.getUser().getName());

                if(user == null){
                    logger.debug("Adding new user: " + follow.getUser().getName());
                    user = userService.addUser(userService.findCommunity(channel), follow.getUser().getName());
                }

                Date followDate = follow.getCreatedAt();
                if (user.getFirstFollowed() == null) {
                    user.setFirstFollowed(followDate);
                }
                user.setLastFollowed(followDate);

                if(followDate != null && followDate.before(user.getFirstSeen())){
                    user.setFirstSeen(followDate);
                }

                if (!user.getAccessLevel().hasAccess(AccessLevel.FOLLOWER)) {
                    user.setAccessLevel(AccessLevel.FOLLOWER);
                }
                if (++count % 20 == 0) {
                    sessionFactory.getCurrentSession().flush();
                    sessionFactory.getCurrentSession().clear();
                }
            }
            if(page.getCursor() != null){
                page = twitchApi.followers(channel.getChannel(), page.getCursor());
            }else{
                page = null;
            }
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
