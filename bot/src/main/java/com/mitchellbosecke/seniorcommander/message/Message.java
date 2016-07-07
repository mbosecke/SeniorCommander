package com.mitchellbosecke.seniorcommander.message;

import com.mitchellbosecke.seniorcommander.channel.Channel;

import java.util.Map;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class Message {

    public enum Type {
        USER_PROVIDED, OUTPUT
    }

    private final Type type;

    private final String content;

    private final String sender;

    private final Channel sourceChannel;

    private final Map<String, Object> meta;

    public Message(Channel sourceChannel, Type type, String sender, String content, Map<String, Object> meta) {
        this.content = content;
        this.sender = sender;
        this.type = type;
        this.sourceChannel = sourceChannel;
        this.meta = meta;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public Type getType() {
        return type;
    }

    public Channel getSourceChannel() {
        return sourceChannel;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }
}

