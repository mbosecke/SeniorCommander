package com.mitchellbosecke.seniorcommander.core.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.Message;
import com.mitchellbosecke.seniorcommander.MessageHandler;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class LoggingHandler implements MessageHandler {

    @Override
    public void handle(Context context, Message message) {
        System.out.println(message.getSender() + ": " + message.getContent());
    }
}
