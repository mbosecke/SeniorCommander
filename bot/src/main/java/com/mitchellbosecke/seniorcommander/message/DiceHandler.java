package com.mitchellbosecke.seniorcommander.message;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class DiceHandler implements MessageHandler {

    private final Pattern pattern = Pattern.compile("!d(\\d+)");

    private final MessageQueue messageQueue;

    public DiceHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void handle(Message message) {
        Matcher matcher = pattern.matcher(message.getContent());
        if (matcher.matches()) {
            Integer num = Integer.valueOf(matcher.group(1));
            Random generator = new Random();
            int result = generator.nextInt(num) + 1;
            messageQueue.addMessage(new Message(Message.Type.OUTPUT, "SeniorCommander", "You are rolled a " + result));
        }
    }
}
