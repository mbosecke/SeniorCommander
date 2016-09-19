package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.*;
import com.mitchellbosecke.seniorcommander.extension.core.service.BettingService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * !bet open option 1, option 2, option 3
 * </p>
 */
public class Betting implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(Betting.class);

    private final UserService userService;

    private final BettingService bettingService;

    private final MessageQueue messageQueue;

    public Betting(MessageQueue messageQueue, BettingService bettingService, UserService userService) {
        this.messageQueue = messageQueue;
        this.bettingService = bettingService;
        this.userService = userService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = bettingService.findCommunity(message.getChannel());

        String subCommand = parsed.getComponents().get(0);

        if ("open".equalsIgnoreCase(subCommand) || "add".equalsIgnoreCase(subCommand)) {

            if (communityModel.getBettingGameModel() != null) {
                messageQueue.add(Message.response(message, "There is already an active bet."));
            } else {
                Set<String> options = new HashSet<>();
                boolean first = true;
                for (String component : parsed.getComponents()) {
                    if (!first) {
                        options.add(component);
                    }
                    first = false;
                }
                if (options.isEmpty()) {
                    messageQueue.add(Message.response(message, "You must provide some options for the bet"));
                } else {
                    bettingService.openBet(communityModel, options);
                    messageQueue.add(Message.shout(message.getChannel(), "A bet has begun!"));
                }
            }
        } else if ("cancel".equalsIgnoreCase(subCommand)) {
            if (communityModel.getBettingGameModel() != null) {
                bettingService.cancelBet(communityModel);
                messageQueue.add(Message.shout(message.getChannel(), "The bet has been cancelled."));
            } else {
                messageQueue.add(Message.response(message, "There is no active bet."));
            }

        } else {
            BettingGameModel game = communityModel.getBettingGameModel();
            if (game != null) {
                for (BettingOptionModel option : game.getOptions()) {
                    if (option.getValue().equalsIgnoreCase(subCommand)) {
                        try {
                            int amount = Integer.parseInt(parsed.getComponents().get(1));
                            CommunityUserModel user = userService.findUser(message.getChannel(), message.getSender());
                            BetModel existingBet = bettingService.getBet(user, game);
                            if (existingBet != null) {
                                messageQueue.add(Message.response(message, String
                                        .format("You've already bet %d on \"%s\"", existingBet.getAmount(), existingBet
                                                .getBettingOptionModel().getValue())));
                            } else {
                                if (user.getPoints() >= amount) {
                                    bettingService.placeBet(user, option, amount);
                                    messageQueue.add(Message.response(message, "Your bet has been placed."));
                                } else {
                                    messageQueue.add(Message.response(message, "You do not have enough points."));
                                }
                            }
                        } catch (NumberFormatException ex) {
                            messageQueue.add(Message.response(message, "That's not a number."));
                        }

                    }
                }
            }
        }
    }

}
