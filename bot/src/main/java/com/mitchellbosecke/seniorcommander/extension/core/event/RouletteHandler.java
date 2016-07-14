package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.PhraseProvider;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class RouletteHandler implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageQueue messageQueue;

    public RouletteHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.USER.equals(message.getType())) {
            if (message.getContent().equalsIgnoreCase("!roulette")) {

                String user = message.getSender();

                boolean survives = Math.random() > 0.5;

                if (survives) {
                    messageQueue.add(Message
                            .response(message, PhraseProvider.getPhrase(PhraseProvider.Category.CLOSE_CALL)));
                } else {
                    messageQueue
                            .add(Message.response(message, PhraseProvider.getPhrase(PhraseProvider.Category.GRIEF)));
                    message.getChannel().timeout(user, 5 * 60l);
                }
            }
        }
    }

}
