package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface UserService extends BaseService {

    CommunityUser findUser(Channel channel, String name);

    CommunityUser setUserOnline(Channel channel, String name);

    CommunityUser setUserOffline(Channel channel, String name);

}
