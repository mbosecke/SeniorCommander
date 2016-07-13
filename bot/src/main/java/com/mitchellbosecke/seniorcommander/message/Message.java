package com.mitchellbosecke.seniorcommander.message;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class Message {

    public enum Type {
        USER, OUTPUT, MEMBERSHIP_NAMES, MEMBERSHIP_JOIN, MEMBERSHIP_PART
    }

    private final Type type;

    private final String content;

    private final String sender;

    private final Channel channel;

    private final boolean whisper;

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

    /**
     * Shouts to all channels.
     *
     * @param content
     * @return
     */
    public static Message shout(String content) {
        return new Message(Type.OUTPUT, null, SeniorCommander.getName(), null, content, false);
    }

    /**
     * Shouts to a specific channel.
     *
     * @param content
     * @return
     */
    public static Message shout(String content, Channel channel) {
        return new Message(Type.OUTPUT, channel, SeniorCommander.getName(), null, content, false);
    }

    /**
     * Responds to a particular message.
     *
     * @param originalMessage
     * @param content
     * @return
     */
    public static Message response(Message originalMessage, String content) {
        return new Message(Type.OUTPUT, originalMessage.channel, SeniorCommander
                .getName(), originalMessage.sender, content, originalMessage.whisper);
    }

    /**
     * A message from a user.
     *
     * @param channel
     * @param sender
     * @param recipient
     * @param content
     * @param whisper
     * @return
     */
    public static Message userInput(Channel channel, String sender, String recipient, String content, boolean whisper) {
        return new Message(Type.USER, channel, sender, recipient, content, whisper);
    }

    public static Message names(Channel channel, String users) {
        return new Message(Type.MEMBERSHIP_NAMES, channel, users, null, null, false);
    }

    public static Message join(Channel channel, String user) {
        return new Message(Type.MEMBERSHIP_JOIN, channel, user, null, null, false);
    }

    public static Message part(Channel channel, String user) {
        return new Message(Type.MEMBERSHIP_PART, channel, user, null, null, false);
    }

}

