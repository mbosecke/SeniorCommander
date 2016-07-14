package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;

import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by mitch_000 on 2016-07-13.
 */
public class RollCommand implements CommandHandler {

    private final MessageQueue messageQueue;

    public RollCommand(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void execute(Message message) {

        StringTokenizer tokenizer = new StringTokenizer(message.getContent());
        tokenizer.nextToken();
        Integer roll = Integer.valueOf(tokenizer.nextToken());
        Random generator = new Random();
        int result = generator.nextInt(roll) + 1;
        messageQueue.add(Message.response(message, "You rolled a " + result));
    }
}
