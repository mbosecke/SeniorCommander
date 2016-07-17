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
public class AdviceCommand implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageQueue messageQueue;

    public AdviceCommand(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void execute(Message message) {
        messageQueue.add(Message.response(message, PhraseProvider.getPhrase(PhraseProvider.Category.ADVICE)));
    }

}
