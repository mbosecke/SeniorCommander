package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.utils.PhraseProvider;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class Roulette implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageQueue messageQueue;

    public Roulette(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void execute(Message message) {

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
