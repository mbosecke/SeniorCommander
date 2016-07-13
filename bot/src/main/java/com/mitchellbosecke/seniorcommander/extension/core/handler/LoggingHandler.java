package com.mitchellbosecke.seniorcommander.extension.core.handler;

import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class LoggingHandler implements MessageHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(Message message) {
        logger.trace(String.format("[%s] %s: %s", message.getType(), message.getSender(), message.getContent()));
    }
}
