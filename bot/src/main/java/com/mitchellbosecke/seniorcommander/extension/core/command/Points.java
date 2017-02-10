package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
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
 * !points
 * </p>
 */
public class Points implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(Points.class);

    private final UserService userService;

    private final SeniorCommander seniorCommander;

    public static final String DEFAULT_POINT_SINGULAR = "point";
    public static final String DEFAULT_POINT_PLURAL = "points";
    public static final String SETTING_POINT_SINGULAR = "points.singular";
    public static final String SETTING_POINT_PLURAL = "points.plural";

    public Points(SeniorCommander seniorCommander, UserService userService) {
        this.seniorCommander = seniorCommander;
        this.userService = userService;
    }

    @Override
    public void execute(Message message) {
        ParsedCommand parsed = new CommandParser().parse(message.getContent());

        MessageQueue messageQueue = seniorCommander.getMessageQueue();
        String pointsName = getPointsName(message);

        if (parsed.getComponents().isEmpty()) {
            CommunityUserModel user = userService.findOrCreateUser(message.getChannel(), message.getSender());
            messageQueue.add(Message.response(message, String.format("you have %d %s.", user.getPoints(), pointsName)));
        } else {
            String subCommand = parsed.getComponents().get(0);

            if ("give".equalsIgnoreCase(subCommand)) {
                attemptToManipulatePoints(true, message, parsed);
            } else if ("take".equalsIgnoreCase(subCommand)) {
                attemptToManipulatePoints(false, message, parsed);
            } else {

                Optional<CommunityUserModel> user = userService
                        .findExistingUser(message.getChannel(), parsed.getComponents().get(0));

                if (user.isPresent()) {
                    messageQueue.add(Message.response(message, String
                            .format("%s has %d %s.", user.get().getName(), user.get().getPoints(), pointsName)));

                } else {
                    messageQueue.add(Message.response(message, "username not found"));
                }
            }
        }

    }

    private void attemptToManipulatePoints(boolean give, Message message, ParsedCommand parsed) {
        MessageQueue messageQueue = seniorCommander.getMessageQueue();
        int amountIndex = -1;
        int amount = -1;
        try {
            amount = Integer.parseInt(parsed.getComponents().get(1));
            amountIndex = 1;
        } catch (NumberFormatException ex) {
            try {
                amount = Integer.parseInt(parsed.getComponents().get(2));
                amountIndex = 2;
            } catch (NumberFormatException ex2) {
            }
        }

        if (amountIndex > -1) { // we've parsed an amount
            int nameIndex = amountIndex == 1 ? 2 : 1;
            String username = parsed.getComponents().get(nameIndex);
            Optional<CommunityUserModel> optionalUser = userService.findExistingUser(message.getChannel(), username);
            if (optionalUser.isPresent()) {
                CommunityUserModel user = optionalUser.get();
                if (give) {
                    user.setPoints(user.getPoints() + amount);
                } else {
                    user.setPoints(Math.max(0, user.getPoints() - amount));
                }

                messageQueue.add(Message.response(message, String
                        .format("%s now has %d %s", username, user.getPoints(), getPointsName(message))));
            } else {
                messageQueue.add(Message.response(message, String.format("Username [%s] does not exist", username)));
            }
        } else {
            messageQueue.add(Message.response(message, "Not a valid number"));
        }

    }

    private String getPointsName(Message message) {
        CommunityModel community = userService.findCommunity(message.getChannel());
        String pointsName = community.getSetting(SETTING_POINT_PLURAL);
        pointsName = pointsName == null ? DEFAULT_POINT_PLURAL : pointsName;
        return pointsName;
    }
}
