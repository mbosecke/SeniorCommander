package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface Channel {

    /**
     * Gets the ID of the corresponding {@link ChannelModel}
     * @return
     */
    long getId();

    String getBotUsername();

    void listen(MessageQueue messageQueue) throws IOException;

    void sendMessage(String content);

    void sendMessage(String recipient, String content);

    void sendWhisper(String recipient, String content);

    void timeout(String user, long duration);

    void shutdown();

    boolean isOnline();

}
