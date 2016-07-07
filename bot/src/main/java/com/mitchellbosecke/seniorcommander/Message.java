package com.mitchellbosecke.seniorcommander;

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

    public Message(Type type, String sender, String content) {
        this.content = content;
        this.sender = sender;
        this.type = type;
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
}

