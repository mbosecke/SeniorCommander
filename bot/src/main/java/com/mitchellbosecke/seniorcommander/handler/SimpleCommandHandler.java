package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class SimpleCommandHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String command;

    private final String reply;

    private final MessageQueue messageQueue;

    public SimpleCommandHandler(MessageQueue messageQueue, String command, String reply) {
        logger.debug("Simple command handler created for: " + command);
        this.messageQueue = messageQueue;
        this.command = command;
        this.reply = reply;
    }

    @Override
    public void handle(Message message) {
        logger.debug("Handling");
        if (message.getContent().startsWith(command)) {
            messageQueue.add(Message.response(message, reply));
        }
    }
}
