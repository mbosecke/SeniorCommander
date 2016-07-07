package com.mitchellbosecke.seniorcommander;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public interface Channel {

    void listen(Context context) throws IOException;

    void sendMessage(Context context, String content);

    void shutdown();

}
