package com.mitchellbosecke.seniorcommander.message;

import com.mitchellbosecke.seniorcommander.Context;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface MessageHandler {

    void handle(Context context, Message message);
}
