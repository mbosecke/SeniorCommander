package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface UserService extends BaseService {

    CommunityUserModel findUser(Channel channel, String name);

    CommunityUserModel setUserOnline(Channel channel, String name);

    CommunityUserModel setUserOffline(Channel channel, String name);

}
