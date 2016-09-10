package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class UserChatHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(UserChatHandler.class);

    private final UserService userService;

    public UserChatHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Message message) {

        if (Message.Type.USER.equals(message.getType())) {
            CommunityUserModel user = userService.setUserOnline(message.getChannel(), message.getSender());
            user.setLastChatted(new Date());
        }
    }

}
