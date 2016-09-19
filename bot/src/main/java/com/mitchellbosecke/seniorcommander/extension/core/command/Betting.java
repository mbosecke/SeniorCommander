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
            if (game != null && parsed.getComponents().size() == 2) {
                boolean betResult = attemptToPlaceBet(game, message, parsed.getComponents().get(0), parsed
                        .getComponents().get(1));

                if (!betResult) {
                    // maybe the user reversed the components
                    betResult = attemptToPlaceBet(game, message, parsed.getComponents().get(1), parsed.getComponents().get(0));

                    if(!betResult){
                        messageQueue.add(Message.response(message, "Kappa"));
                    }
                }
            }
        }
    }

    private boolean attemptToPlaceBet(BettingGameModel game, Message message, String optionString,
                                      String amountString) {

        boolean parsedOption = false;
        boolean parsedAmount = false;
        int amount = -1;
        BettingOptionModel chosenOption = null;

        try {
            amount = Integer.parseInt(amountString);
            parsedAmount = true;
        } catch (NumberFormatException ex) {
        }

        for (BettingOptionModel option : game.getOptions()) {
            if (option.getValue().equalsIgnoreCase(optionString)) {
                chosenOption = option;
                parsedOption = true;
            }
        }

        if (parsedAmount || parsedOption) {
            if (!parsedAmount) {
                messageQueue.add(Message.response(message, "That's not a number. This isn't a joke."));
            }

            if (!parsedOption) {
                messageQueue.add(Message.response(message, "That's not an option. Please pay attention."));
            }

            if (parsedAmount && parsedOption) {
                CommunityUserModel user = userService.findUser(message.getChannel(), message.getSender());
                BetModel existingBet = bettingService.getBet(user, game);
                if (existingBet != null) {
                    messageQueue.add(Message.response(message, String
                            .format("You've already bet %d on \"%s\"", existingBet.getAmount(), existingBet
                                    .getBettingOptionModel().getValue())));
                } else {
                    if (user.getPoints() >= amount) {
                        bettingService.placeBet(user, chosenOption, amount);
                        messageQueue.add(Message.response(message, "Your bet has been placed."));
                    } else {
                        messageQueue.add(Message.response(message, "You don't have enough points. RIP."));
                    }
                }
            }
        }
        return parsedAmount || parsedOption;
    }

}
