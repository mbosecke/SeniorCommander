package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.GiveawayModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.GiveawayService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class GiveawayKeywordHandler implements EventHandler {

    private final static Logger logger = LoggerFactory.getLogger(GiveawayKeywordHandler.class);

    private final GiveawayService giveawayService;

    private final UserService userService;

    public GiveawayKeywordHandler(GiveawayService giveawayService, UserService userService) {
        this.giveawayService = giveawayService;
        this.userService = userService;
    }

    @Override
    public void handle(Message message) {
        if(Message.Type.USER == message.getType()) {

            Channel channel = message.getChannel();
            GiveawayModel giveaway = giveawayService.findActiveGiveaway(giveawayService.findCommunity(channel));
            if(giveaway != null && giveaway.getKeyword().equalsIgnoreCase(message.getContent())){
                giveawayService.enterGiveaway(userService.findOrCreateUser(channel, message.getSender()));
            }
        }
    }
}
