package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.domain.ChannelConfiguration;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.repository.CommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class NamesHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(NamesHandler.class);

    private final CommunityService communityService;

    public NamesHandler(CommunityService communityService) {
        this.communityService = communityService;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.MEMBERSHIP_NAMES.equals(message.getType())) {

            ChannelConfiguration channelConfiguration = communityService
                    .find(ChannelConfiguration.class, message.getChannel().getId());
            channelConfiguration.getOnlineUsers().clear();

            String[] usernames = message.getSender().split(",");
            for (String username : usernames) {
                communityService.setUserOnline(message.getChannel(), username);
            }
        }
    }
}
