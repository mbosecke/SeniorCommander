package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.AuctionModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.AuctionService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
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

    private final SeniorCommander seniorCommander;

    private final UserService userService;

    public Auction(SeniorCommander seniorCommander, AuctionService auctionService, UserService userService) {
        this.seniorCommander = seniorCommander;
        this.auctionService = auctionService;
        this.userService = userService;
    }

    @Override
    public void execute(Message message) {
        MessageQueue messageQueue = seniorCommander.getMessageQueue();

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = auctionService.findCommunity(message.getChannel());
        AuctionModel existingAuction = auctionService.findActiveAuction(communityModel);

        if ("!bid".equalsIgnoreCase(parsed.getTrigger())) {
            Integer amount = null;
            try {
                amount = Integer.parseInt(parsed.getComponents().get(0));
            } catch (Exception ex) {
                messageQueue.add(Message.response(message, "That is not a valid bid"));
            }

            if (amount != null) {
                CommunityUserModel user = userService.findOrCreateUser(message.getChannel(), message.getSender());
                if (user.getPoints() >= amount) {

                    if (existingAuction.getWinningBid() != null && amount <= existingAuction.getWinningBid()) {
                        messageQueue.add(Message.response(message, String
                                .format("The current highest bid is %d", existingAuction.getWinningBid())));
                    } else {

                        auctionService.placeBid(existingAuction, user, amount);

                        messageQueue.add(Message.response(message, String
                                .format("You are the new highest bidder with a bid of %d %s.", amount, getPointsName(message))));
                    }

                } else {
                    messageQueue.add(Message.response(message, String
                            .format("You don't have enough %s. RIP.", getPointsName(message))));
                }

            }
        } else {

            // provide a description of the bet
            if (parsed.getComponents().isEmpty()) {
                if (existingAuction == null) {
                    messageQueue.add(Message.response(message, "There is no ongoing auction."));
                } else if (existingAuction.getWinningCommunityUserModel() != null) {
                    messageQueue.add(Message.response(message, String
                            .format("The current auction is for \"%s\" and the highest bidder is currently %s with a bid of %d %s.", existingAuction
                                    .getPrize(), existingAuction.getWinningCommunityUserModel()
                                    .getName(), existingAuction.getWinningBid(), getPointsName(message))));
                } else {
                    messageQueue.add(Message.response(message, String
                            .format("The current auction is for \"%s\" but no bids have been made yet", existingAuction
                                    .getPrize())));
                }

            } else {
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
                        auctionService.cancelAuction(existingAuction);
                        messageQueue.add(Message.shout(message.getChannel(), "The auction has been cancelled."));
                    } else {
                        messageQueue.add(Message.response(message, "There is no active auction."));
                    }

                } else if ("close".equalsIgnoreCase(subCommand)) {
                    // declare the winner
                    if (existingAuction != null) {
                        Optional<CommunityUserModel> winner = auctionService.close(existingAuction);

                        if (winner.isPresent()) {
                            messageQueue.add(Message.response(message, String
                                    .format("The auction is closed and we have a winner. The winner is %s.", winner
                                            .get().getName())));
                        } else {
                            messageQueue.add(Message.response(message, "The auction is closed; there were no bids."));
                        }
                    } else {
                        messageQueue.add(Message.response(message, "There isn't an active auction."));
                    }
                }
            }
        }

    }

    private String getPointsName(Message message) {
        CommunityModel community = userService.findCommunity(message.getChannel());
        String pointsName = community.getSetting(Points.SETTING_POINT_PLURAL);
        pointsName = pointsName == null ? Points.DEFAULT_POINT_PLURAL : pointsName;
        return pointsName;
    }

}
