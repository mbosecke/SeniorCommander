package com.mitchellbosecke.seniorcommander.message;

import com.mitchellbosecke.seniorcommander.channel.Channel;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class Message {

    private final String content;

    private final String sender;

    private final Class<? extends Channel> channelClass;

    public Message(String sender, String content, Class<? extends Channel> channelClass) {
        this.content = content;
        this.sender = sender;
        this.channelClass = channelClass;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public Class<? extends Channel> getChannelClass() {
        return channelClass;
    }
}

