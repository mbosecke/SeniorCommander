package com.mitchellbosecke.seniorcommander.message;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class PrintMessageHandler implements MessageHandler {

    @Override
    public void handle(Message message) {
        System.out.println(message.getSender() + ": " + message.getContent());
    }
}
