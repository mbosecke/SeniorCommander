package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelConfiguration;
import com.mitchellbosecke.seniorcommander.domain.Community;
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
    public void persist(Object entity) {
        sessionFactory.getCurrentSession().persist(entity);
    }

    @Override
    public Community findCommunity(Channel channel) {
        ChannelConfiguration channelConfig = find(ChannelConfiguration.class, channel.getId());
        return channelConfig.getCommunity();
    }
}
