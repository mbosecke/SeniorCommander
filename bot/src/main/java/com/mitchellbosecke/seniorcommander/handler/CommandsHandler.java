package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.repository.CommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class CommandsHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(CommandsHandler.class);

    private final Pattern commandsPattern = Pattern.compile("!commands\\s+(.*)");

    private final CommunityService communityService;

    private final MessageQueue messageQueue;

    public CommandsHandler(CommunityService communityService, MessageQueue messageQueue) {
        this.communityService = communityService;
        this.messageQueue = messageQueue;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.USER.equals(message.getType())) {
            Matcher matcher = commandsPattern.matcher(message.getContent());
            if (matcher.matches()) {

                StringTokenizer tokenizer = new StringTokenizer(matcher.group(1));

                String subCommand = tokenizer.nextToken();
                String commandName = tokenizer.nextToken();
                String output = tokenizer.nextToken();

                if ("add".equalsIgnoreCase(subCommand)) {
                    logger.debug("Command has been added: " + commandName);
                    messageQueue.add(Message.response(message, "The command has been added."));
                } else {
                    logger.debug("Unknown command");
                    // TODO: confusion emotion
                }

            }
        }

    }

}
