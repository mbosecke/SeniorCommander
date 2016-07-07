package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class OutputHandler implements MessageHandler {

    @Override
    public void handle(Context context, Message message) {
        if (Message.Type.OUTPUT.equals(message.getType())) {
            context.getChannels().get(0).sendMessage(context, message.getContent());
        }
    }
}
