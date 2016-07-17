package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.domain.ChannelConfiguration;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class NamesHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(NamesHandler.class);

    private final UserService userService;

    public NamesHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.MEMBERSHIP_NAMES.equals(message.getType())) {

            ChannelConfiguration channelConfiguration = userService
                    .find(ChannelConfiguration.class, message.getChannel().getId());
            channelConfiguration.getOnlineUsers().clear();

            String[] usernames = message.getSender().split(",");
            for (String username : usernames) {
                userService.setUserOnline(message.getChannel(), username);
            }
        }
    }
}
