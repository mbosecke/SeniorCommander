package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.GiveawayModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.GiveawayService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * <p>
 * !giveaway open keyword="foo"
 * !giveaway close
 * !giveaway draw
 * </p>
 */
public class Giveaway implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(Giveaway.class);

    private final GiveawayService giveawayService;

    private final MessageQueue messageQueue;

    private final String[] keywordOption = {"keyword", "kw"};

    public Giveaway(MessageQueue messageQueue, GiveawayService giveawayService) {
        this.messageQueue = messageQueue;
        this.giveawayService = giveawayService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = giveawayService.findCommunity(message.getChannel());
        GiveawayModel existingGiveaway = giveawayService.findActiveGiveaway(communityModel);

        // provide a description of the bet
        if (!parsed.getComponents().isEmpty()) {

            String subCommand = parsed.getComponents().get(0);

            if ("open".equalsIgnoreCase(subCommand)) {
                // open a new giveaway

                if (existingGiveaway != null) {
                    messageQueue.add(Message.response(message, "There is already an active giveaway."));
                } else {
                    String keyword = parsed.getOption(keywordOption);
                    if (keyword == null) {
                        messageQueue.add(Message.response(message, "You must provide a keyword"));
                    } else {
                        giveawayService.openGiveaway(communityModel, keyword);
                        messageQueue.add(Message.shout(message.getChannel(), "A giveaway has begun!"));
                    }
                }

            } else if ("cancel".equalsIgnoreCase(subCommand)) {
                // cancel an existing giveaway

                if (existingGiveaway != null) {
                    giveawayService.cancelGiveaway(communityModel);
                    messageQueue.add(Message.shout(message.getChannel(), "The giveaway has been cancelled."));
                } else {
                    messageQueue.add(Message.response(message, "There is no active giveaway."));
                }

            } else if ("close".equalsIgnoreCase(subCommand)) {
                // close a bet
                if (existingGiveaway != null) {
                    existingGiveaway.setClosed(new Date());
                    messageQueue.add(Message.shout(message.getChannel(), "The giveaway has been closed."));
                } else {
                    messageQueue.add(Message.response(message, "There is no active giveaway."));
                }

            } else if ("draw".equalsIgnoreCase(subCommand)) {
                // declare the winning option

                GiveawayModel mostRecentGiveaway = giveawayService.findMostRecentGiveaway(communityModel);
                if (mostRecentGiveaway != null) {
                    String winner = giveawayService.drawWinner(mostRecentGiveaway);
                    messageQueue.add(Message.response(message, String
                            .format("The giveaway is closed and a winner has been chosen. The winner is %s.", winner)));
                } else {
                    messageQueue.add(Message.response(message, "There has never been a giveaway."));
                }
            }
        }
    }

}
