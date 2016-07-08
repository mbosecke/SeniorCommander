package com.mitchellbosecke.seniorcommander.message;

import com.mitchellbosecke.seniorcommander.channel.Channel;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class Message {

    public enum Type {
        USER, OUTPUT
    }

    private final Type type;

    private final String content;

    private final String sender;

    private final Channel channel;

    private final Boolean whisper;

    private final String recipient;

    private Message(Type type, Channel channel, String sender, String recipient, String content, Boolean whisper) {
        this.content = content;
        this.sender = sender;
        this.type = type;
        this.channel = channel;
        this.whisper = whisper;
        this.recipient = recipient;
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

    public Channel getChannel() {
        return channel;
    }

    public Boolean isWhisper() {
        return whisper;
    }

    public String getRecipient() {
        return recipient;
    }

    public static Message shout(String content) {
        return new Builder().type(Type.OUTPUT).content(content).build();
    }

    public static Message response(Message originalMessage, String content) {
        return new Builder().channel(originalMessage.channel).type(Type.OUTPUT).recipient(originalMessage.sender)
                .content(content).whisper(originalMessage.whisper).build();
    }

    public static class Builder {

        private Channel channel;
        private Type type;
        private String sender;
        private String recipient;
        private String content;
        private Boolean whisper;

        public Builder channel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder recipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder whisper(Boolean whisper) {
            this.whisper = whisper;
            return this;
        }

        public Message build() {
            whisper = whisper == null ? false : whisper;
            return new Message(type, channel, sender, recipient, content, whisper);
        }
    }
}

