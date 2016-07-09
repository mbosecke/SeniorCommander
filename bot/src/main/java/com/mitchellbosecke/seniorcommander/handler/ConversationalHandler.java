package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class ConversationalHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(Context context, Message message) {
        if (SeniorCommander.getName().equals(message.getRecipient()) && !message.getContent().startsWith("!")) {
            context.getMessageQueue().add(Message.response(message, "Hello friend!"));
        }
    }
}
