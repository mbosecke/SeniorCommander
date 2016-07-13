package com.mitchellbosecke.seniorcommander.extension.core.handler;

import com.mitchellbosecke.seniorcommander.PhraseProvider;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class AdviceHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageQueue messageQueue;

    public AdviceHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void handle(Message message) {

        if (Message.Type.USER.equals(message.getType())) {
            if (message.getContent().equalsIgnoreCase("!advice")) {
                messageQueue.add(Message.response(message, PhraseProvider.getPhrase(PhraseProvider.Category.ADVICE)));
            }
        }
    }

}
