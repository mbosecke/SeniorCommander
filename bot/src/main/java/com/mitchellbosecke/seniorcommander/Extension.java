package com.mitchellbosecke.seniorcommander;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public interface Extension {

    List<MessageHandler> getMessageHandlers();

    List<Channel> getChannels();

    // TODO: scheduled tasks
}
