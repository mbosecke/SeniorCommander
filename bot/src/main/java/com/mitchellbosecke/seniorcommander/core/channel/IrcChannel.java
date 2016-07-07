package com.mitchellbosecke.seniorcommander.core.channel;

import com.mitchellbosecke.seniorcommander.*;
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

    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    /**
     * If shutdown was called before channel had an attempt to startup
     */
    private boolean interrupted = false;

    private Context context;

    @Override
    public void listen(Context context) throws IOException {
        synchronized (startupLock) {
            if (!interrupted) {
                this.context = context;
                Configuration configuration = context.getConfiguration();
                this.setName(configuration.getProperty(CONFIG_IRC_USERNAME));
                try {
                    this.connect(configuration.getProperty(CONFIG_IRC_SERVER), Integer.valueOf(configuration.getProperty(CONFIG_IRC_PORT)), configuration.getProperty(CONFIG_IRC_OAUTH_KEY));
                } catch (IrcException e) {
                    throw new RuntimeException(e);
                }
                this.joinChannel(configuration.getProperty(CONFIG_IRC_CHANNEL));
                System.out.println("Listening");
            }
        }
    }

    @Override
    public void sendMessage(Context context, String content) {
        String channel = context.getConfiguration().getProperty(CONFIG_IRC_CHANNEL);
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
        context.getMessageQueue().addMessage(new Message(Message.Type.USER_PROVIDED, sender, message));
    }
}
