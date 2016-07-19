package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.domain.Command;
import com.mitchellbosecke.seniorcommander.domain.CommandLog;
import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface CommandService extends BaseService {

    void addCommand(Community community, String trigger, String message, long cooldown, AccessLevel accessLevel);

    void deleteCommand(Community community, String trigger);

    CommandLog findMostRecentCommandLog(Command command, CommunityUser communityUser);

    Command findCommand(Community community, String trigger);

}
