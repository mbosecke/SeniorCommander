package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class JoinPartHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(JoinPartHandler.class);

    private final UserService userService;

    public JoinPartHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Message message) {

        if (Message.Type.MEMBERSHIP_JOIN.equals(message.getType())) {
            userService.setUserOnline(message.getChannel(), message.getSender());
        } else if (Message.Type.MEMBERSHIP_PART.equals(message.getType())) {
            userService.setUserOffline(message.getChannel(), message.getSender());
        }
    }

}
