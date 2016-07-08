package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class ConversationalHandler  implements MessageHandler {

    @Override
    public void handle(Context context, Message message) {

        if(SeniorCommander.class.getName().equals(message.getRecipient())){
            context.getMessageQueue().add(Message.response(message, "Hello friend!"));
        }
    }
}
