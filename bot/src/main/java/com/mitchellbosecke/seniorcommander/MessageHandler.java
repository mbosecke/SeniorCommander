package com.mitchellbosecke.seniorcommander;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface MessageHandler {

    void handle(Context context, Message message);
}
