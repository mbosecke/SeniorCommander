package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;

import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by mitch_000 on 2016-07-13.
 */
public class Roll implements CommandHandler {

    private final SeniorCommander seniorCommander;

    public Roll(SeniorCommander seniorCommander) {
        this.seniorCommander = seniorCommander;
    }

    @Override
    public void execute(Message message) {

        StringTokenizer tokenizer = new StringTokenizer(message.getContent());
        tokenizer.nextToken();
        Integer roll = Integer.valueOf(tokenizer.nextToken());
        Random generator = new Random();
        int result = generator.nextInt(roll) + 1;
        seniorCommander.getMessageQueue().add(Message.response(message, "You rolled a " + result));
    }
}
