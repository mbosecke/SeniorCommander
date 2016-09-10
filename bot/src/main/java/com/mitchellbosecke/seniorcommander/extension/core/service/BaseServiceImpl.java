package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import org.hibernate.SessionFactory;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class BaseServiceImpl implements BaseService {

    protected final SessionFactory sessionFactory;

    public BaseServiceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <T> T find(Class<T> clazz, long id) {
        return sessionFactory.getCurrentSession().find(clazz, id);
    }

    @Override
    public <T> void delete(Class<T> clazz, long id) {
        T obj = find(clazz, id);
        sessionFactory.getCurrentSession().delete(obj);
    }

    @Override
    public void delete(Object entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void persist(Object entity) {
        sessionFactory.getCurrentSession().persist(entity);
    }

    @Override
    public CommunityModel findCommunity(Channel channel) {
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());
        return channelModel.getCommunityModel();
    }
}
