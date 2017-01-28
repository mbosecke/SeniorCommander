package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;

/**
 * Created by mitch_000 on 2016-07-17.
 */
public class BaseService {

    public <T> T find(Class<T> clazz, long id) {
        return TransactionManager.getCurrentSession().find(clazz, id);
    }

    public <T> void delete(Class<T> clazz, long id) {
        T obj = find(clazz, id);
        TransactionManager.getCurrentSession().delete(obj);
    }

    public void delete(Object entity) {
        TransactionManager.getCurrentSession().delete(entity);
    }

    public void persist(Object entity) {
        TransactionManager.getCurrentSession().persist(entity);
    }

    public CommunityModel findCommunity(Channel channel) {
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());
        return channelModel.getCommunityModel();
    }
}
