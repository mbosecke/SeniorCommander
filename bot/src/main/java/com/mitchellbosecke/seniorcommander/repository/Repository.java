package com.mitchellbosecke.seniorcommander.repository;

import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import com.mitchellbosecke.seniorcommander.message.Message;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface Repository {

    Community findCommunity(Message message);

    CommunityUser findSender(Message message);

    CommunityUser findUser(Community community, String name);

    <T> T find(Class<T> clazz, long id);

    void persist(Object entity);

}
