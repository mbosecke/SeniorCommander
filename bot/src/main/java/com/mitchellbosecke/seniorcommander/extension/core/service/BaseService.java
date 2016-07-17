package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.Community;

/**
 * Created by mitch_000 on 2016-07-17.
 */
public interface BaseService {

    Community findCommunity(Channel channel);

    <T> T find(Class<T> clazz, long id);

    void persist(Object entity);
}
