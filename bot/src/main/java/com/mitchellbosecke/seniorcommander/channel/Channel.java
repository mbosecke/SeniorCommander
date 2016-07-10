package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.domain.ChannelConfiguration;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface Channel {

    /**
     * Gets the ID of the corresponding {@link ChannelConfiguration}
     * @return
     */
    long getId();

    void listen(MessageQueue messageQueue) throws IOException;

    void sendMessage(String content);

    void sendMessage(String recipient, String content);

    void sendWhisper(String recipient, String content);

    void timeout(String user, long duration);

    void shutdown();

}
