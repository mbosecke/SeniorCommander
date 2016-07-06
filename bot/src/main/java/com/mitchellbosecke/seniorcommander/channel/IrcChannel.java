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

    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    /**
     * If shutdown was called before channel had an attempt to startup
     */
    private boolean interrupted = false;

    private final Configuration configuration;

    public IrcChannel(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void listen(MessageQueue messageQueue) throws IOException {
        synchronized (startupLock) {
            if (!interrupted) {
                this.messageQueue = messageQueue;
                this.setName(configuration.getProperty(CONFIG_IRC_USERNAME));
                try {
                    this.connect(configuration.getProperty(CONFIG_IRC_SERVER), Integer.valueOf(configuration.getProperty(CONFIG_IRC_PORT)), configuration.getProperty(CONFIG_IRC_OAUTH_KEY));
                } catch (IrcException e) {
                    throw new RuntimeException(e);
                }
                this.joinChannel(configuration.getProperty(CONFIG_IRC_CHANNEL));
            }
        }
    }

    @Override
    public void sendMessage(String content) {
        String channel = configuration.getProperty(CONFIG_IRC_CHANNEL);
        this.sendMessage(channel, content);
    }

    @Override
    public void shutdown() {
        synchronized (startupLock) {
            System.out.println("Shutting down.");
            if (this.isConnected()) {
                this.disconnect();
                this.quitServer();
                this.dispose();
            } else {
                this.interrupted = true;
            }
        }

    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        messageQueue.addMessage(new Message(Message.Type.USER_PROVIDED, sender, message));
    }
}
