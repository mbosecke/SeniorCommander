package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.message.MessageQueue;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface Channel {

    void listen(MessageQueue messageQueue) throws IOException;

    void sendMessage(String content);

    void shutdown();

}
