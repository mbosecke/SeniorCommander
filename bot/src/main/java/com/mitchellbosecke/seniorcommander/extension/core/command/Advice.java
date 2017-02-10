package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.utils.PhraseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class Advice implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SeniorCommander seniorCommander;

    public Advice(SeniorCommander seniorCommander) {
        this.seniorCommander = seniorCommander;
    }

    @Override
    public void execute(Message message) {
        seniorCommander.getMessageQueue()
                .add(Message.response(message, PhraseProvider.getPhrase(PhraseProvider.Category.ADVICE)));
    }

}
