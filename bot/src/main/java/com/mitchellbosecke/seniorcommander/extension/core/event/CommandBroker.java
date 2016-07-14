package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.Command;
import com.mitchellbosecke.seniorcommander.domain.CommandLog;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommunityService;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class CommandBroker implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(CommandBroker.class);

    private final CommunityService communityService;

    private final MessageQueue messageQueue;

    private final Map<String, CommandHandler> commandHandlers;

    public CommandBroker(CommunityService communityService, MessageQueue messageQueue,
                         List<CommandHandler> commandHandlers) {
        this.communityService = communityService;
        this.messageQueue = messageQueue;
        this.commandHandlers = commandHandlers.stream()
                .collect(Collectors.toMap(handler -> handler.getClass().getName(), handler -> handler));
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.USER.equals(message.getType())) {
            StringTokenizer tokenizer = new StringTokenizer(message.getContent());
            Command command = communityService.findCommand(message.getChannel(), tokenizer.nextToken());

            if (command != null) {
                CommunityUser user = communityService.findUser(message.getChannel(), message.getSender());
                if (command.getCooldown() > 0) {

                    CommandLog commandLog = communityService.findMostRecentCommandLog(command, user);
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
        communityService.persist(log);
    }

}
