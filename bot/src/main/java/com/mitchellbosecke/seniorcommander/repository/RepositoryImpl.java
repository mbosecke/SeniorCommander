package com.mitchellbosecke.seniorcommander.repository;

import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class RepositoryImpl implements Repository {

    private final SessionFactory sessionFactory;

    public RepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Community findCommunity(Message message) {
        Community community = null;

        try {
            //@formatter:off
             community = sessionFactory.getCurrentSession().createQuery("SELECT DISTINCT c " +
                    "FROM Community c " +
                    "JOIN c.channelConfigurations ccs " +
                    "WHERE ccs.id = :channelId", Community.class)
                    .setParameter("channelId", message.getChannel().getId())
                    .getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            // do nothing;
        }

        return community;
    }

    @Override
    public CommunityUser findSender(Message message) {
        CommunityUser user = null;
        try {
            //@formatter:off
            user = sessionFactory.getCurrentSession().createQuery("SELECT DISTINCT cu " +
                    "FROM CommunityUser cu " +
                    "JOIN cu.community c " +
                    "JOIN c.channelConfigurations ccs " +
                    "WHERE cu.name = :name " +
                    "AND ccs.id = :channelId", CommunityUser.class)
                    .setParameter("name", message.getSender())
                    .setParameter("channelId", message.getChannel().getId())
                    .getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            // do nothing
        }
        return user;
    }

    @Override
    public CommunityUser findUser(Community community, String name) {
        try {
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT cu FROM CommunityUser cu WHERE cu.community = :community AND cu.name = " + ":name", CommunityUser.class)
                    .setParameter("community", community).setParameter("name", name).getSingleResult();
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
