package com.mitchellbosecke.seniorcommander.message;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface MessageHandler {

    void handle(Message message);
}
