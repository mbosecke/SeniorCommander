package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.*;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class CommunityServiceImpl implements CommunityService {

    private final SessionFactory sessionFactory;

    public CommunityServiceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CommandLog findMostRecentCommandLog(Command command, CommunityUser communityUser) {

        CommandLog log = null;
        try {
            //@formatter:off
            log = sessionFactory.getCurrentSession()
                    .createQuery("SELECT cl " +
                            "FROM CommandLog cl " +
                            "WHERE cl.command = :command " +
                            "AND cl.communityUser = :user " +
                            "ORDER BY cl.logDate desc ", CommandLog.class)
                    .setParameter("command", command).setParameter("user", communityUser).setMaxResults(1)
                    .getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
        }
        return log;
    }

    @Override
    public CommunityUser findUser(Channel channel, String name) {
        ChannelConfiguration channelConfig = find(ChannelConfiguration.class, channel.getId());
        Community community = channelConfig.getCommunity();
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT cu " +
                            "FROM CommunityUser cu " +
                            "WHERE cu.community = :community " +
                            "AND lower(cu.name) = :name ", CommunityUser.class)
                    .setParameter("community", community).setParameter("name", name.toLowerCase()).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public Command findCommand(Channel channel, String trigger) {
        ChannelConfiguration channelConfig = find(ChannelConfiguration.class, channel.getId());
        Community community = channelConfig.getCommunity();
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT c " +
                            "FROM Command c " +
                            "WHERE c.community = :community " +
                            "AND c.trigger = :trigger ", Command.class)
                    .setParameter("community", community).setParameter("trigger", trigger.toLowerCase()).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public CommunityUser setUserOnline(Channel channel, String name) {
        ChannelConfiguration channelConfig = find(ChannelConfiguration.class, channel.getId());
        Community community = channelConfig.getCommunity();
        CommunityUser user = findUser(community, name);
        if (user == null) {
            user = addUser(community, name);
        }
        if (!channelConfig.getOnlineUsers().contains(user)) {
            channelConfig.getOnlineUsers().add(user);
        }
        return user;
    }

    @Override
    public CommunityUser setUserOffline(Channel channel, String name) {
        ChannelConfiguration channelConfig = find(ChannelConfiguration.class, channel.getId());
        Community community = channelConfig.getCommunity();
        CommunityUser user = findUser(community, name);
        if (user == null) {
            user = addUser(community, name);
        }
        if (channelConfig.getOnlineUsers().contains(user)) {
            channelConfig.getOnlineUsers().remove(user);
        }
        return user;
    }

    private CommunityUser addUser(Community community, String name) {
        CommunityUser user = new CommunityUser();
        user.setCommunity(community);
        user.setName(name.toLowerCase());
        user.setFirstSeen(new Date());
        user.setAccessLevel(CommunityUser.AccessLevel.USER.name());
        persist(user);
        return user;
    }

    private CommunityUser findUser(Community community, String name) {
        try {
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT cu FROM CommunityUser cu WHERE cu.community = :community AND cu.name = :name", CommunityUser.class)
                    .setParameter("community", community).setParameter("name", name.toLowerCase()).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public <T> T find(Class<T> clazz, long id) {
        return sessionFactory.getCurrentSession().find(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        sessionFactory.getCurrentSession().persist(entity);
    }
}
