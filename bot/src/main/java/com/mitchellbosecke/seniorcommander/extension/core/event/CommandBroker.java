package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.domain.Command;
import com.mitchellbosecke.seniorcommander.domain.CommandLog;
import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
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
            Community community = commandService.findCommunity(message.getChannel());
            Command command = commandService.findCommand(community, message.getContent());

            if (command != null && command.isEnabled()) {
                CommunityUser user = userService.findUser(message.getChannel(), message.getSender());
                if (hasPermission(command, user)) {
                    executeAfterCooldown(message, command, user);
                }
            }
        }
    }

    private boolean hasPermission(Command command, CommunityUser user) {
        return user.getAccessLevel().hasAccess(command.getAccessLevel());
    }

    private void executeAfterCooldown(Message message, Command command, CommunityUser user) {
        if (command.getCooldown() > 0) {

            CommandLog commandLog = commandService.findMostRecentCommandLog(command, user);
            if (commandLog == null) {
                executeCommand(message, command, user);
            } else {
                long cooldownMilliseconds = command.getCooldown() * 1000;
                if (commandLog.getLogDate().getTime() + cooldownMilliseconds <= new Date().getTime()) {
                    executeCommand(message, command, user);
                }
            }
        } else {
            executeCommand(message, command, user);
        }
    }

    private void executeCommand(Message message, Command command, CommunityUser user) {

        if (command.getImplementation() != null) {
            CommandHandler commandHandler = commandHandlers.get(command.getImplementation());
            commandHandler.execute(message);
        } else {
            messageQueue.add(Message.shout(command.getMessage(), message.getChannel()));
        }

        // log this use
        CommandLog log = new CommandLog();
        log.setCommand(command);
        log.setCommunityUser(user);
        log.setLogDate(new Date());
        userService.persist(log);
    }

}
