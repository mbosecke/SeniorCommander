package com.mitchellbosecke.seniorcommander.extension.core.handler;

import com.mitchellbosecke.seniorcommander.domain.Command;
import com.mitchellbosecke.seniorcommander.domain.CommandLog;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class CustomCommandHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(CustomCommandHandler.class);

    private final CommunityService communityService;

    private final MessageQueue messageQueue;

    public CustomCommandHandler(CommunityService communityService, MessageQueue messageQueue) {
        this.communityService = communityService;
        this.messageQueue = messageQueue;
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
        messageQueue.add(Message.shout(command.getMessage(), message.getChannel()));

        // log this use
        CommandLog log = new CommandLog();
        log.setCommand(command);
        log.setCommunityUser(user);
        log.setLogDate(new Date());
        communityService.persist(log);
    }

}
