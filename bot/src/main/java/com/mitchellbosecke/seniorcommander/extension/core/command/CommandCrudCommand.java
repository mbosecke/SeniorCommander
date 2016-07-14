package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommunityService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class CommandCrudCommand implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(CommandCrudCommand.class);

    private final Pattern commandsPattern = Pattern.compile("!commands\\s+(.*)");

    private final CommunityService communityService;

    private final MessageQueue messageQueue;

    public CommandCrudCommand(CommunityService communityService, MessageQueue messageQueue) {
        this.communityService = communityService;
        this.messageQueue = messageQueue;
    }

    @Override
    public void execute(Message message) {

        StringTokenizer tokenizer = new StringTokenizer(message.getContent());

        String trigger = tokenizer.nextToken();
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
