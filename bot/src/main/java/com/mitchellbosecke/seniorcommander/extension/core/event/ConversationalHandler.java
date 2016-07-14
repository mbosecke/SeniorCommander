package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.EventHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class ConversationalHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageQueue messageQueue;

    public ConversationalHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void handle(Message message) {
        if(Message.Type.USER.equals(message.getType())) {
            if (SeniorCommander.getName().equalsIgnoreCase(message.getRecipient()) && !message.getContent().startsWith("!")) {
                messageQueue.add(Message.response(message, "Hello friend!"));
            }
        }
    }
}
