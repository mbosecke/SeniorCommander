package com.mitchellbosecke.seniorcommander.repository;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.Command;
import com.mitchellbosecke.seniorcommander.domain.CommandLog;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface CommunityService {

    CommandLog findMostRecentCommandLog(Command command, CommunityUser communityUser);

    CommunityUser findUser(Channel channel, String name);

    Command findCommand(Channel channel, String trigger);

    CommunityUser setUserOnline(Channel channel, String name);

    CommunityUser setUserOffline(Channel channel, String name);

    <T> T find(Class<T> clazz, long id);

    void persist(Object entity);

}
