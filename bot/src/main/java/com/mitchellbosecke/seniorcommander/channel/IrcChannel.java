package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.Configuration;
import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class IrcChannel extends PircBot implements Channel {

    Logger logger = LoggerFactory.getLogger(getClass());

    private static final String CONFIG_IRC_USERNAME = "irc.username";
    private static final String CONFIG_IRC_SERVER = "irc.server";
    private static final String CONFIG_IRC_PORT = "irc.port";
    private static final String CONFIG_IRC_OAUTH_KEY = "irc.oauthkey";
    private static final String CONFIG_IRC_CHANNEL = "irc.channel";

    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    private volatile boolean running = true;

    private Context context;

    @Override
    public void listen(Context context) throws IOException {
        synchronized (startupLock) {
            if (running) {
                this.context = context;
                Configuration configuration = context.getConfiguration();

                if(configuration.getProperty(CONFIG_IRC_SERVER) != null) {
                    this.setName(configuration.getProperty(CONFIG_IRC_USERNAME));
                    try {
                        connect(configuration.getProperty(CONFIG_IRC_SERVER), Integer.valueOf(configuration.getProperty(CONFIG_IRC_PORT)), configuration
                                .getProperty(CONFIG_IRC_OAUTH_KEY));
                        logger.debug("IRC server connected");
                    } catch (IrcException e) {
                        throw new RuntimeException(e);
                    }
                    this.joinChannel(configuration.getProperty(CONFIG_IRC_CHANNEL));
                    logger.debug("IRC channel listening");
                    running = true;
                }
            }
        }
    }

    @Override
    protected void onServerResponse(int code, String response) {
        logger.debug(String.format("[%d] %s", code, response));
    }

    @Override
    protected void onConnect() {
        // required to receive whispers
        sendRawLine("CAP REQ :twitch.tv/commands");

        //sendRawLine("CAP REQ :twitch.tv/membership");
    }

    @Override
    protected void onUnknown(String line) {
        IrcProtocolMessage ircProtocolMessage = new IrcProtocolMessage(line);

        if ("WHISPER".equals(ircProtocolMessage.getCommand())) {
            this.onPrivateMessage(ircProtocolMessage.getNick(), ircProtocolMessage.getLogin(), ircProtocolMessage
                    .getHostname(), ircProtocolMessage.getLastParam());
        } else {
            logger.debug("Received unknown command: " + line);
        }
    }

    @Override
    public void sendMessage(Context context, String content) {
        if(running) {
            String channel = context.getConfiguration().getProperty(CONFIG_IRC_CHANNEL);
            this.sendMessage(channel, content);
        }
    }

    @Override
    public void sendMessage(Context context, String recipient, String content) {
        if(running) {
            String channel = context.getConfiguration().getProperty(CONFIG_IRC_CHANNEL);
            this.sendMessage(channel, "@" + recipient + " " + content);
        }
    }

    @Override
    public void sendWhisper(Context context, String recipient, String content) {
        if(running) {
            String channel = context.getConfiguration().getProperty(CONFIG_IRC_CHANNEL);
            sendRawLineViaQueue(String.format("PRIVMSG %s :/w %s %s", channel, recipient, content));
        }
    }

    @Override
    public void shutdown() {
        synchronized (startupLock) {
            logger.debug("IRC Channel shutting down");
            if (this.isConnected()) {
                this.disconnect();
                this.quitServer();
                this.dispose();
            } else {
                this.running = false;
            }
        }
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        String botUsername = context.getConfiguration().getProperty(CONFIG_IRC_USERNAME);
        if (botUsername.equalsIgnoreCase(sender)) {
            return;
        }
        logger.trace("Received whisper on IRC Channel: " + message);
        context.getMessageQueue().add(Message.userInput(this, sender, SeniorCommander.getName(), message, true));
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        String botUsername = context.getConfiguration().getProperty(CONFIG_IRC_USERNAME);
        if (botUsername.equalsIgnoreCase(sender)) {
            return;
        }
        logger.trace("Received message on IRC Channel: " + message);

        String[] split = ChannelUtils.splitRecipient(message);
        String recipient = split[0];
        message = split[1];

        if (botUsername.equalsIgnoreCase(recipient)) {
            recipient = SeniorCommander.class.getName();
        }

        context.getMessageQueue().add(Message.userInput(this, sender, recipient, message, false));
    }
}
