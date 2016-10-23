package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class UserService extends BaseService {

    public UserService(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public CommunityUserModel findOrCreateUser(Channel channel, String name) {
        CommunityUserModel user;
        try {
            user = findUser(channel, name);
        } catch (NoResultException ex) {
            user = new CommunityUserModel();
            user.setCommunityModel(findCommunity(channel));
            user.setName(name.toLowerCase());
            user.setFirstSeen(ZonedDateTime.now(ZoneId.of("UTC")));
            user.setAccessLevel(AccessLevel.USER);
            persist(user);
        }
        return user;
    }

    public Optional<CommunityUserModel> findExistingUser(Channel channel, String name) {

        CommunityUserModel user = null;
        try {
            user = findUser(channel, name);
        } catch (NoResultException ex) {
        }
        return Optional.ofNullable(user);
    }

    private CommunityUserModel findUser(Channel channel, String name) throws NoResultException {
        CommunityModel communityModel = findCommunity(channel);
        //@formatter:off
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT cu " +
                        "FROM CommunityUserModel cu " +
                        "WHERE cu.communityModel = :community " +
                        "AND lower(cu.name) = :name ", CommunityUserModel.class)
                .setParameter("community", communityModel).setParameter("name", name.toLowerCase()).getSingleResult();
        //@formatter:on
    }

    public CommunityUserModel setUserOnline(Channel channel, String name) {
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());
        CommunityUserModel user = findOrCreateUser(channel, name);
        user.setLastOnline(ZonedDateTime.now(ZoneId.of("UTC")));
        if (!channelModel.getOnlineUsers().contains(user)) {
            channelModel.getOnlineUsers().add(user);
        }
        return user;
    }

    public CommunityUserModel setUserOffline(Channel channel, String name) {
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());
        CommunityUserModel user = findOrCreateUser(channel, name);
        if (channelModel.getOnlineUsers().contains(user)) {
            channelModel.getOnlineUsers().remove(user);
        }

        Duration duration = Duration.between(user.getLastOnline(), ZonedDateTime.now(ZoneId.of("UTC")));
        user.setTimeOnline(user.getTimeOnline() + (duration.toMillis()/1000));

        return user;
    }


    public void giveOnlineUsersPoints(Channel channel, int points) {
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());
        for (CommunityUserModel user : channelModel.getOnlineUsers()) {
            if (!user.isBot()) {
                user.setPoints(user.getPoints() + points);
            }
        }
    }
}
