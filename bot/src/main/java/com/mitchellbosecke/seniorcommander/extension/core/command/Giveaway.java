package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.GiveawayModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.GiveawayService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

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

    private final SeniorCommander seniorCommander;

    private final String[] keywordOption = {"keyword", "kw"};

    public Giveaway(SeniorCommander seniorCommander, GiveawayService giveawayService) {
        this.seniorCommander = seniorCommander;
        this.giveawayService = giveawayService;
    }

    @Override
    public void execute(Message message) {

        MessageQueue messageQueue = seniorCommander.getMessageQueue();
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
                    existingGiveaway.setClosed(ZonedDateTime.now(ZoneId.of("UTC")));
                    messageQueue.add(Message.shout(message.getChannel(), "The giveaway has been closed."));
                } else {
                    messageQueue.add(Message.response(message, "There is no active giveaway."));
                }

            } else if ("draw".equalsIgnoreCase(subCommand)) {
                // declare the winning option

                GiveawayModel mostRecentGiveaway = giveawayService.findMostRecentGiveaway(communityModel);
                if (mostRecentGiveaway != null) {
                    Optional<String> winner = giveawayService.drawWinner(mostRecentGiveaway);

                    if (winner.isPresent()) {
                        messageQueue.add(Message.response(message, String
                                .format("The giveaway is closed and a winner has been chosen. The winner is %s.", winner
                                        .get())));
                    } else {
                        messageQueue.add(Message.response(message, "The giveaway is over; there were no entries."));
                    }
                } else {
                    messageQueue.add(Message.response(message, "There has never been a giveaway."));
                }
            }
        }
    }

}
