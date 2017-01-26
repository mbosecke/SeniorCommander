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

import javax.persistence.NoResultException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        logger.trace("Started follow audit.");

        String twitchClientId = ConfigFactory.load().getConfig("seniorcommander").getString("twitch.clientId");
        TwitchApi twitchApi = new TwitchApi(twitchClientId);

        CommunityUserModel latestFollower = null;
        try {
            latestFollower = sessionFactory.getCurrentSession()
                    .createQuery("SELECT u FROM CommunityUserModel u WHERE u.lastFollowed IS NOT NULL AND u.unfollowed IS NULL AND u.communityModel = :communityModel ORDER BY u.lastFollowed DESC NULLS LAST", CommunityUserModel.class)
                    .setParameter("communityModel", userService.findCommunity(channel)).setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }

        int numberOfFollowers = 0;
        try {
            numberOfFollowers = ((Long) sessionFactory.getCurrentSession()
                    .createQuery("SELECT count(*) FROM CommunityUserModel u WHERE u.lastFollowed IS NOT NULL AND u.unfollowed IS NULL AND u.communityModel = :communityModel")
                    .setParameter("communityModel", userService.findCommunity(channel)).uniqueResult()).intValue();
        } catch (NoResultException ex2) {
        }

        ChannelFollowsPage page = twitchApi.followers(channel.getChannel());
        String latestPageFollower = page.getFollows().isEmpty() ? null : page.getFollows().get(0).getUser().getName();

        if (numberOfFollowers == page
                .getTotal() && latestFollower != null && latestPageFollower != null && latestFollower.getName()
                .equalsIgnoreCase(latestPageFollower)) {
            // no need to perform a full audit
            logger.trace("Followers haven't changed");
        } else {
            performFullAudit(twitchApi, page);
            logger.trace("Follower audit complete");
        }
    }

    private void performFullAudit(TwitchApi twitchApi, ChannelFollowsPage page) {
        List<ChannelFollow> actualFollowers = new ArrayList<>();
        while (page != null && !page.getFollows().isEmpty()) {
            actualFollowers.addAll(page.getFollows());

            if (page.getCursor() != null) {
                page = twitchApi.followers(channel.getChannel(), page.getCursor());
            } else {
                break;
            }
        }
        // sort by name
        actualFollowers.sort((o1, o2) -> o1.getUser().getName().compareToIgnoreCase(o2.getUser().getName()));

        // get database followers sorted by name
        //@formatter:off
        List<String> databaseFollowers = sessionFactory.getCurrentSession()
                .createQuery("" +
                        "SELECT u.name " +
                        "FROM CommunityUserModel u " +
                        "WHERE u.lastFollowed IS NOT NULL " +
                        "AND u.unfollowed IS NULL " +
                        "AND u.communityModel = :communityModel " +
                        "ORDER BY u.name ASC", String.class)
                .setParameter("communityModel", userService.findCommunity(channel)).getResultList();
        //@formatter:on

        // postgresql ignores underscores when sorting so we need to sort ourselves
        Collections.sort(databaseFollowers);

        int actualFollowerIndex = 0;
        int databaseFollowerIndex = 0;

        if(databaseFollowers.isEmpty()){
            while (actualFollowerIndex < actualFollowers.size()) {
                markAsFollower(actualFollowers.get(actualFollowerIndex));
                actualFollowerIndex++;
            }
        }else {

            while (true) {
                ChannelFollow actualFollower = actualFollowers.get(actualFollowerIndex);
                String databaseFollower = databaseFollowers.get(databaseFollowerIndex);

                if (actualFollower.getUser().getName().equalsIgnoreCase(databaseFollower)) {
                    actualFollowerIndex++;
                    databaseFollowerIndex++;
                } else if (actualFollower.getUser().getName().compareToIgnoreCase(databaseFollower) < 0) {
                    markAsFollower(actualFollower);
                    actualFollowerIndex++;
                } else {
                    markAsUnfollowed(databaseFollower);
                    databaseFollowerIndex++;
                }

                if (actualFollowerIndex >= actualFollowers.size()) {
                    // loop through remaining database followers and mark them as unfollowed
                    while (databaseFollowerIndex < databaseFollowers.size()) {
                        markAsUnfollowed(databaseFollowers.get(databaseFollowerIndex));
                        databaseFollowerIndex++;
                    }
                    break;
                } else if (databaseFollowerIndex >= databaseFollowers.size()) {
                    // loop throw remaining actual followers and mark them as new followers
                    while (actualFollowerIndex < actualFollowers.size()) {
                        markAsFollower(actualFollowers.get(actualFollowerIndex));
                        actualFollowerIndex++;
                    }
                    break;
                }
            }
        }
    }

    private void markAsFollower(ChannelFollow channelFollow) {
        CommunityUserModel user = userService.findOrCreateUser(channel, channelFollow.getUser().getName());
        logger.debug("Marking " + channelFollow.getUser().getName() + " as a follower");

        ZonedDateTime followDate = ZonedDateTime.ofInstant(channelFollow.getCreatedAt().toInstant(), ZoneId.of("UTC"));
        if (user.getFirstFollowed() == null) {
            user.setFirstFollowed(followDate);
        }
        user.setLastFollowed(followDate);

        if (followDate.isBefore(user.getFirstSeen())) {
            user.setFirstSeen(followDate);
        }

        if (!user.getAccessLevel().hasAccess(AccessLevel.FOLLOWER)) {
            user.setAccessLevel(AccessLevel.FOLLOWER);
        }

        user.setUnfollowed(null);
    }

    private void markAsUnfollowed(String username) {
        CommunityUserModel user = userService.findOrCreateUser(channel, username);
        logger.debug("Marking " + username + " as unfollowed");

        if (user.getAccessLevel() == AccessLevel.FOLLOWER) {
            user.setAccessLevel(AccessLevel.USER);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        user.setUnfollowed(now);
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
