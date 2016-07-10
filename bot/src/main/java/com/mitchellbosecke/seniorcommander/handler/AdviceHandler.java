package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.PhraseProvider;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class AdviceHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(Context context, Message message) {
        if (message.getContent().equalsIgnoreCase("!advice")) {

            context.getMessageQueue()
                    .add(Message.response(message, PhraseProvider.getPhrase(PhraseProvider.Category.ADVICE)));

        }
    }

}
