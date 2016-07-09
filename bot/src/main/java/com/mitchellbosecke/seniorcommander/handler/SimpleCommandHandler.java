package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class SimpleCommandHandler implements MessageHandler {

    private final String command;

    private final String reply;

    public SimpleCommandHandler(String command, String reply){
        this.command = command;
        this.reply = reply;
    }

    @Override
    public void handle(Context context, Message message) {
        if(message.getContent().startsWith(command)){
            context.getMessageQueue().add(Message.response(message, reply));
        }
    }
}
