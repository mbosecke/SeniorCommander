package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.message.Message;

/**
 * Created by mitch_000 on 2016-07-13.
 */
public interface CommandHandler {

    void execute(Message message);
}
