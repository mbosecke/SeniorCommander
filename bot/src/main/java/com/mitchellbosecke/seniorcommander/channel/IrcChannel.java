package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.Configuration;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class IrcChannel extends PircBot implements Channel {

    private static final String CONFIG_IRC_USERNAME = "irc.username";
    private static final String CONFIG_IRC_SERVER = "irc.server";
    private static final String CONFIG_IRC_PORT = "irc.port";
    private static final String CONFIG_IRC_OAUTH_KEY = "irc.oauthkey";
    private static final String CONFIG_IRC_CHANNEL = "irc.channel";

    private MessageQueue messageQueue;

    @Override
    public void listen(Configuration configuration, MessageQueue messageQueue) throws IOException {
        this.messageQueue = messageQueue;


        this.setName(configuration.getProperty(CONFIG_IRC_USERNAME));
        try {
            this.connect(configuration.getProperty(CONFIG_IRC_SERVER), Integer.valueOf(configuration.getProperty(CONFIG_IRC_PORT)), configuration.getProperty(CONFIG_IRC_OAUTH_KEY));
        } catch (IrcException e) {
            throw new RuntimeException(e);
        }
        this.joinChannel(configuration.getProperty(CONFIG_IRC_CHANNEL));
    }


    @Override
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        super.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
        System.out.println("onQuit");
    }

    @Override
    protected void onDisconnect() {
        super.onDisconnect();
        System.out.println("onDisconnect");
    }

    @Override
    public void shutdown() {
        this.disconnect();
        this.quitServer();
        System.out.println("Is connected: " + this.isConnected());
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        messageQueue.addMessage(new Message(sender, message, getClass()));
    }
}
