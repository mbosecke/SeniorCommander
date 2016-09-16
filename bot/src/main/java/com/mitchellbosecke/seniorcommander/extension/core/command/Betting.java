package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.BettingService;
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

    private final BettingService bettingService;

    private final MessageQueue messageQueue;

    public Betting(MessageQueue messageQueue, BettingService bettingService) {
        this.messageQueue = messageQueue;
        this.bettingService = bettingService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = bettingService.findCommunity(message.getChannel());

        String subCommand = parsed.getComponents().get(0);

        if ("open".equalsIgnoreCase(subCommand) || "add".equalsIgnoreCase(subCommand)) {

            if(communityModel.getBettingGameModel() != null){
                messageQueue.add(Message.response(message, "There is already an active bet."));
            }else {
                Set<String> options = new HashSet<>();
                boolean first = true;
                for (String component : parsed.getComponents()) {
                    if (!first) {
                        options.add(component);
                    }
                    first = false;
                }
                if(options.isEmpty()){
                    messageQueue.add(Message.response(message, "You must provide some options for the bet"));
                }else {
                    bettingService.openBet(communityModel, options);
                    messageQueue.add(Message.shout(message.getChannel(), "A bet has begun!"));
                }
            }
        }else if ("cancel".equalsIgnoreCase(subCommand)){
            bettingService.cancelBet(communityModel);
            messageQueue.add(Message.shout(message.getChannel(), "The bet has been cancelled."));
        }
    }

}