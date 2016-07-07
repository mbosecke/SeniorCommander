package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.Context;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface Channel {

    void listen(Context context) throws IOException;

    void sendMessage(Context context, String content);

    void sendMessage(Context context, String recipient, String content);

    void sendWhisper(Context context, String recipient, String content);

    void shutdown();

}
