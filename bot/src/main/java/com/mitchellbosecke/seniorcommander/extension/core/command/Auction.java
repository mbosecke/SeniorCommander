package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.AuctionModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.AuctionService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * <p>
 * !auction open "prize"
 * !auction cancel
 * !auction close
 * !bid 100
 * </p>
 */
public class Auction implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(Auction.class);

    private final AuctionService auctionService;

    private final MessageQueue messageQueue;

    public Auction(MessageQueue messageQueue, AuctionService auctionService) {
        this.messageQueue = messageQueue;
        this.auctionService = auctionService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = auctionService.findCommunity(message.getChannel());
        AuctionModel existingAuction = auctionService.findActiveAuction(communityModel);

        // provide a description of the bet
        if (!parsed.getComponents().isEmpty()) {

            String subCommand = parsed.getComponents().get(0);

            if ("open".equalsIgnoreCase(subCommand)) {
                // open a new auction

                if (existingAuction != null) {
                    messageQueue.add(Message.response(message, "There is already an active auction."));
                } else {

                    if (parsed.getComponents().size() < 2) {
                        messageQueue.add(Message.response(message, "You must provide a prize"));
                    } else {
                        // TODO: multi word prizes
                        String prize = parsed.getComponents().get(1);
                        auctionService.openAuction(communityModel, prize);
                        messageQueue.add(Message.shout(message.getChannel(), "An auction has begun!"));
                    }
                }

            } else if ("cancel".equalsIgnoreCase(subCommand)) {
                // cancel an existing giveaway

                if (existingAuction != null) {
                    auctionService.cancelAuction(communityModel);
                    messageQueue.add(Message.shout(message.getChannel(), "The auction has been cancelled."));
                } else {
                    messageQueue.add(Message.response(message, "There is no active auction."));
                }

            } else if ("close".equalsIgnoreCase(subCommand)) {
                // declare the winner
                if (existingAuction != null) {
                    Optional<CommunityUserModel> winner = auctionService.close(existingAuction);

                    if(winner.isPresent()) {
                        messageQueue.add(Message.response(message, String.format("The auction is closed and we have a winner. The winner is %s.", winner.get())));
                    }else{
                        messageQueue.add(Message.response(message,"The auction is closed; there were no bids."));
                    }
                } else {
                    messageQueue.add(Message.response(message, "There isn't an active auction."));
                }
            }
        }
    }

}
