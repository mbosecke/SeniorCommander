package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.domain.Command;
import com.mitchellbosecke.seniorcommander.domain.CommandLog;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.repository.CommunityService;
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
                messageQueue.add(Message.shout(command.getMessage()));

                // log this use
                CommandLog log = new CommandLog();
                log.setCommand(command);
                log.setCommunityUser(communityService.findUser(message.getChannel(), message.getSender()));
                log.setLogDate(new Date());
                communityService.persist(log);
            }
        }

    }

}
