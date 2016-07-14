package com.mitchellbosecke.seniorcommander.message;

import com.mitchellbosecke.seniorcommander.message.Message;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface EventHandler {

    void handle(Message message);
}
