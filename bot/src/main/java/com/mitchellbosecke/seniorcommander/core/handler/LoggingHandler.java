package com.mitchellbosecke.seniorcommander.core.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.Message;
import com.mitchellbosecke.seniorcommander.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class LoggingHandler implements MessageHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(Context context, Message message) {
        logger.trace(message.getSender() + ": " + message.getContent());
    }
}
