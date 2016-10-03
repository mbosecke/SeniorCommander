package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * !points
 * </p>
 */
public class Points implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(Points.class);

    private final UserService userService;

    private final MessageQueue messageQueue;

    public Points(MessageQueue messageQueue, UserService userService) {
        this.messageQueue = messageQueue;
        this.userService = userService;
    }

    @Override
    public void execute(Message message) {
        ParsedCommand parsed = new CommandParser().parse(message.getContent());

        if (parsed.getComponents().isEmpty()) {
            CommunityUserModel user = userService.findUser(message.getChannel(), message.getSender());
            messageQueue.add(Message.response(message, String.format("you have %d points.", user.getPoints())));
        } else {
            CommunityUserModel user = userService.findUser(message.getChannel(), parsed.getComponents().get(0));

            if (user == null) {
                messageQueue.add(Message.response(message, "username not found"));
            } else {
                messageQueue
                        .add(Message.response(message, String.format("%s has %d points.", user.getName(), user.getPoints())));
            }
        }

    }

}
