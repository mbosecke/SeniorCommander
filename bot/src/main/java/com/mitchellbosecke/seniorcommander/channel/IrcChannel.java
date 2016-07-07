package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.Configuration;
import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.StringTokenizer;

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
                    connect(configuration.getProperty(CONFIG_IRC_SERVER), Integer
                            .valueOf(configuration.getProperty(CONFIG_IRC_PORT)), configuration
                            .getProperty(CONFIG_IRC_OAUTH_KEY));
                    logger.debug("IRC server connected");
                } catch (IrcException e) {
                    throw new RuntimeException(e);
                }
                this.joinChannel(configuration.getProperty(CONFIG_IRC_CHANNEL));
                logger.debug("IRC channel listening");
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
        StringTokenizer tokenizer = new StringTokenizer(line);
        String senderInfo = tokenizer.nextToken();
        String command = tokenizer.nextToken();

        String sourceNick = "";
        String sourceLogin = "";
        String sourceHostname = "";

        int exclamation = senderInfo.indexOf("!");
        int at = senderInfo.indexOf("@");
        if (senderInfo.startsWith(":")) {
            if (exclamation > 0 && at > 0 && exclamation < at) {
                sourceNick = senderInfo.substring(1, exclamation);
                sourceLogin = senderInfo.substring(exclamation + 1, at);
                sourceHostname = senderInfo.substring(at + 1);
            }
        }

        command = command.toUpperCase();
        if ("WHISPER".equals(command)) {
            this.onPrivateMessage(sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
        } else {
            logger.debug("Received unknown command: " + line);
        }
    }

    @Override
    public void sendMessage(Context context, String content) {
        String channel = context.getConfiguration().getProperty(CONFIG_IRC_CHANNEL);
        this.sendMessage(channel, content);
    }

    @Override
    public void sendMessage(Context context, String recipient, String content) {
        String channel = context.getConfiguration().getProperty(CONFIG_IRC_CHANNEL);
        this.sendMessage(channel, "@" + recipient + " " + content);
    }

    @Override
    public void sendWhisper(Context context, String recipient, String content) {
        String channel = context.getConfiguration().getProperty(CONFIG_IRC_CHANNEL);
        sendRawLineViaQueue(String.format("PRIVMSG %s :/w %s %s", channel, recipient, content));
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
                this.interrupted = true;
            }
        }
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        if (context.getConfiguration().getProperty(CONFIG_IRC_USERNAME).equalsIgnoreCase(sender)) {
            return;
        }
        logger.trace("Received whisper on IRC Channel: " + message);
        context.getMessageQueue()
                .add(new Message.Builder().channel(this).type(Message.Type.USER).user(sender).content(message)
                        .whisper(true).build());
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (context.getConfiguration().getProperty(CONFIG_IRC_USERNAME).equalsIgnoreCase(sender)) {
            return;
        }
        logger.trace("Received message on IRC Channel: " + message);
        context.getMessageQueue()
                .add(new Message.Builder().channel(this).type(Message.Type.USER).user(sender).content(message)
                        .whisper(false).build());
    }
}
