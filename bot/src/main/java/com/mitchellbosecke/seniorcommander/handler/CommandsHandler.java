package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
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

    @Override
    public void handle(Context context, Message message) {
        Matcher matcher = commandsPattern.matcher(message.getContent());
        if (matcher.matches()) {

            StringTokenizer tokenizer = new StringTokenizer(matcher.group(1));

            String subCommand = tokenizer.nextToken();
            String commandName = tokenizer.nextToken();

            if ("add".equalsIgnoreCase(subCommand)) {
                logger.debug("Command has been added: " + commandName);
                context.getMessageQueue().add(Message.response(message, "The command has been added."));
                context.getSeniorCommander().registerHandler(new SimpleCommandHandler(commandName, tokenizer.nextToken()));
            } else {
                logger.debug("Unknown command");
                // TODO: confusion emotion
            }

        }
    }

}
