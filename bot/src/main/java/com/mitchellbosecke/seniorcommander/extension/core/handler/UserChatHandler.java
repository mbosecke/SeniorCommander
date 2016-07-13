package com.mitchellbosecke.seniorcommander.extension.core.handler;

import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class UserChatHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(UserChatHandler.class);

    private final CommunityService communityService;

    public UserChatHandler(CommunityService communityService) {
        this.communityService = communityService;
    }

    @Override
    public void handle(Message message) {

        if (Message.Type.USER.equals(message.getType())) {
            CommunityUser user = communityService.setUserOnline(message.getChannel(), message.getSender());
            user.setLastChatted(new Date());
        }
    }

}
