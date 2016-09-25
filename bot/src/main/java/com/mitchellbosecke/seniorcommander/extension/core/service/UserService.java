package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class UserService extends BaseService {

    public UserService(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public CommunityUserModel findUser(Channel channel, String name) {
        CommunityModel communityModel = findCommunity(channel);
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT cu " +
                            "FROM CommunityUserModel cu " +
                            "WHERE cu.communityModel = :community " +
                            "AND lower(cu.name) = :name ", CommunityUserModel.class)
                    .setParameter("community", communityModel).setParameter("name", name.toLowerCase()).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

    public CommunityUserModel setUserOnline(Channel channel, String name) {
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());
        CommunityModel communityModel = channelModel.getCommunityModel();
        CommunityUserModel user = findUser(communityModel, name);
        if (user == null) {
            user = addUser(communityModel, name);
        }
        user.setLastOnline(new Date());
        if (!channelModel.getOnlineUsers().contains(user)) {
            channelModel.getOnlineUsers().add(user);
        }
        return user;
    }

    public CommunityUserModel setUserOffline(Channel channel, String name) {
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());
        CommunityModel communityModel = channelModel.getCommunityModel();
        CommunityUserModel user = findUser(communityModel, name);
        if (user == null) {
            user = addUser(communityModel, name);
        }
        if (channelModel.getOnlineUsers().contains(user)) {
            channelModel.getOnlineUsers().remove(user);
        }
        return user;
    }

    private CommunityUserModel addUser(CommunityModel communityModel, String name) {
        CommunityUserModel user = new CommunityUserModel();
        user.setCommunityModel(communityModel);
        user.setName(name.toLowerCase());
        user.setFirstSeen(new Date());
        user.setAccessLevel(AccessLevel.USER);
        persist(user);
        return user;
    }

    private CommunityUserModel findUser(CommunityModel communityModel, String name) {
        try {
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT cu FROM CommunityUserModel cu WHERE cu.communityModel = :community AND cu" +
                            ".name = " +
                            ":name", CommunityUserModel.class)
                    .setParameter("community", communityModel).setParameter("name", name.toLowerCase()).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public void giveOnlineUsersPoints(Channel channel, int points) {
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());
        for(CommunityUserModel user : channelModel.getOnlineUsers()){
            user.setPoints(user.getPoints() + points);
        }
    }
}
