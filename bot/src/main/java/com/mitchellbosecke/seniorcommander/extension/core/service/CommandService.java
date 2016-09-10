package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.domain.CommandModel;
import com.mitchellbosecke.seniorcommander.domain.CommandLogModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface CommandService extends BaseService {

    void addCommand(CommunityModel communityModel, String trigger, String message, long cooldown, AccessLevel accessLevel);

    void deleteCommand(CommunityModel communityModel, String trigger);

    CommandLogModel findMostRecentCommandLog(CommandModel commandModel, CommunityUserModel communityUserModel);

    CommandModel findCommand(CommunityModel communityModel, String trigger);

}
