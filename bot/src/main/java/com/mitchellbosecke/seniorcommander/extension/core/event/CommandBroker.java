package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.domain.CommandModel;
import com.mitchellbosecke.seniorcommander.domain.CommandLogModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommandService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class CommandBroker implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(CommandBroker.class);

    private final UserService userService;

    private final CommandService commandService;

    private final MessageQueue messageQueue;

    private final Map<String, CommandHandler> commandHandlers;

    public CommandBroker(MessageQueue messageQueue, List<CommandHandler> commandHandlers, UserService userService,
                         CommandService commandService) {
        this.userService = userService;
        this.commandService = commandService;
        this.messageQueue = messageQueue;
        this.commandHandlers = commandHandlers.stream()
                .collect(Collectors.toMap(handler -> handler.getClass().getName(), handler -> handler));
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.USER.equals(message.getType())) {
            CommunityModel communityModel = commandService.findCommunity(message.getChannel());
            CommandModel commandModel = commandService.findCommand(communityModel, message.getContent());

            if (commandModel != null && commandModel.isEnabled()) {
                CommunityUserModel user = userService.findUser(message.getChannel(), message.getSender());
                if (hasPermission(commandModel, user)) {
                    executeAfterCooldown(message, commandModel, user);
                }
            }
        }
    }

    private boolean hasPermission(CommandModel commandModel, CommunityUserModel user) {
        return user.getAccessLevel().hasAccess(commandModel.getAccessLevel());
    }

    private void executeAfterCooldown(Message message, CommandModel commandModel, CommunityUserModel user) {
        if (commandModel.getCooldown() > 0) {

            CommandLogModel commandLogModel = commandService.findMostRecentCommandLog(commandModel, user);
            if (commandLogModel == null) {
                executeCommand(message, commandModel, user);
            } else {
                long cooldownMilliseconds = commandModel.getCooldown() * 1000;
                if (commandLogModel.getLogDate().getTime() + cooldownMilliseconds <= new Date().getTime()) {
                    executeCommand(message, commandModel, user);
                }
            }
        } else {
            executeCommand(message, commandModel, user);
        }
    }

    private void executeCommand(Message message, CommandModel commandModel, CommunityUserModel user) {

        if (commandModel.getImplementation() != null) {
            CommandHandler commandHandler = commandHandlers.get(commandModel.getImplementation());
            commandHandler.execute(message);
        } else {
            messageQueue.add(Message.shout(commandModel.getMessage(), message.getChannel()));
        }

        // log this use
        CommandLogModel log = new CommandLogModel();
        log.setCommandModel(commandModel);
        log.setCommunityUserModel(user);
        log.setLogDate(new Date());
        userService.persist(log);
    }

}
