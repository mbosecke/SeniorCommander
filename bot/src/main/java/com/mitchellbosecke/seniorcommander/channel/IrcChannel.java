package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.message.MessageUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class IrcChannel extends ListenerAdapter implements Channel {

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    private volatile boolean running = true;



    private final String server;

    private final String username;

    private final String channel;

    private final Integer port;

    private final String password;


    private MessageQueue messageQueue;

    private PircBotX ircClient;

    public IrcChannel(String server, Integer port, String username, String password, String channel) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
        this.channel = channel;
    }

    @Override
    public void listen(MessageQueue messageQueue) throws IOException {
        synchronized (startupLock) {
            if (running) {
                this.messageQueue = messageQueue;

                org.pircbotx.Configuration configuration = new org.pircbotx.Configuration.Builder()
                        .setName(username)
                        .setServerPassword(password)
                        .addServer(server, port)
                        .addListener(this)
                        .addAutoJoinChannel(channel)
                        .addCapHandler(new EnableCapHandler("twitch.tv/commands"))
                        .buildConfiguration();

                ircClient = new PircBotX(configuration);


                logger.debug("IRC channel listening");
                running = true;
                try {
                    ircClient.startBot();
                } catch (IrcException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Public chat message
     *
     * @param event
     * @throws Exception
     */
    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        if (username.equalsIgnoreCase(event.getUser().getNick())) { // ignore messages from this bot
            return;
        }
        logger.trace("Received message on IRC Channel: " + event.getMessage());

        String[] split = MessageUtils.splitRecipient(event.getMessage());
        String recipient = split[0];
        String message = split[1];

        if (username.equalsIgnoreCase(recipient)) {
            recipient = SeniorCommander.class.getName();
        }

        messageQueue.add(Message.userInput(this, event.getUser().getNick(), recipient, message, false));
    }

    /**
     * Whisper
     *
     * @param event
     * @throws Exception
     */
    @Override
    public void onUnknown(UnknownEvent event) throws Exception {
        IrcProtocolMessage ircProtocolMessage = new IrcProtocolMessage(event.getLine());
        if ("WHISPER".equals(ircProtocolMessage.getCommand())) {

            if (username.equalsIgnoreCase(ircProtocolMessage.getNick())) {
                return;
            }
            logger.trace("Received whisper on IRC Channel: " + ircProtocolMessage.getLastParam());
            messageQueue.add(Message.userInput(this, ircProtocolMessage.getNick(), SeniorCommander.getName(),
                    ircProtocolMessage.getLastParam(),
                    true));
        } else {
            logger.debug("Received unknown command: " + ircProtocolMessage.getCommand());
        }
    }

    @Override
    public void sendMessage(String content) {
        if (running) {
            //this.sendMessage(content);
            //ircClient.sendMessage("");
            ircClient.sendIRC().message(channel, content);
        }
    }

    @Override
    public void sendMessage(String recipient, String content) {
        if (running) {
            //this.sendMessage("@" + recipient + " " + content);
            ircClient.sendIRC().message(channel, "@" + recipient + ", " + content);
        }
    }

    @Override
    public void sendWhisper(String recipient, String content) {
        if (running) {
            ircClient.sendRaw().rawLine(String.format("PRIVMSG %s :/w %s %s", channel, recipient, content));
        }
    }

    @Override
    public void shutdown() {
        synchronized (startupLock) {
            if (running) {
                running = false;
                logger.debug("IRC Channel shutting down");
                ircClient.sendIRC().quitServer();
                //ircClient.close();
                //this.disconnect();
                //this.quitServer();
                //this.dispose();
            }
        }
    }



}
