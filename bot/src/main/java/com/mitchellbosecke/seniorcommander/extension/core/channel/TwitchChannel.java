package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.message.MessageUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class TwitchChannel extends ListenerAdapter implements Channel {

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    private volatile boolean running = true;

    private final long id;

    private final String server;

    private final String username;

    private final String channel;

    private final Integer port;

    private final String password;

    private volatile boolean lurk = false;

    private MessageQueue messageQueue;

    private PircBotX ircClient;

    private volatile boolean online = false;

    public TwitchChannel(long id, String server, Integer port, String username, String password, String channel) {
        this.id = id;
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

                org.pircbotx.Configuration configuration = new org.pircbotx.Configuration.Builder().setName(username)
                        .setServerPassword(password).addServer(server, port).addListener(this).setAutoNickChange(false)
                        .setOnJoinWhoEnabled(false).setCapEnabled(true)
                        .addCapHandler(new EnableCapHandler("twitch.tv/commands"))
                        .addCapHandler(new EnableCapHandler("twitch.tv/membership")).addAutoJoinChannel(channel)
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
            recipient = SeniorCommander.getName();
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
            messageQueue.add(Message
                    .userInput(this, ircProtocolMessage.getNick(), SeniorCommander.getName(), ircProtocolMessage
                            .getLastParam(), true));
        } else {
            logger.debug("Received unknown command: " + ircProtocolMessage.getCommand());
        }
    }

    @Override
    public void onNotice(NoticeEvent event) throws Exception {
        logger.trace("Notice event: " + event.getNotice());
        messageQueue.add(Message.modList(this, event.getNotice()));
    }

    @Override
    public void onJoin(JoinEvent event) throws Exception {
        logger.trace("Join event: " + event.getUser().getNick());
        messageQueue.add(Message.join(this, event.getUser().getNick()));
    }

    @Override
    public void onPart(PartEvent event) throws Exception {
        logger.trace("Part event: " + event.getUser().getNick());
        messageQueue.add(Message.part(this, event.getUser().getNick()));
    }

    @Override
    public void onUserList(UserListEvent event) throws Exception {
        Set<User> users = event.getUsers();
        StringBuilder names = new StringBuilder();
        for (User user : users) {
            names.append(user.getNick()).append(",");
        }
        messageQueue.add(Message.names(this, names.substring(0, names.length() - 1)));
        logger.debug("User list: " + names.toString());
    }

    public void getModList(){
        ircClient.sendIRC().message(channel, "/mods");
    }

    @Override
    public void sendMessage(String content) {
        logger.debug("Twitch channel send message 1");
        if (running && !lurk) {
            logger.debug("Twitch channel sending message 2");
            ircClient.sendIRC().message(channel, content);
        }
    }

    @Override
    public void sendMessage(String recipient, String content) {
        if (running && !lurk) {
            ircClient.sendIRC().message(channel, "@" + recipient + ", " + content);
        }
    }

    @Override
    public void sendWhisper(String recipient, String content) {
        if (running && !lurk) {
            ircClient.sendRaw().rawLine(String.format("PRIVMSG %s :/w %s %s", channel, recipient, content));
        }
    }

    @Override
    public void timeout(String user, long duration) {
        if (running&& !lurk) {
            ircClient.sendIRC().message(channel, String.format(".timeout %s %d", user, duration));
        }
    }

    @Override
    public void shutdown() {
        synchronized (startupLock) {
            if (running) {
                running = false;
                logger.debug("IRC Channel shutting down");
                ircClient.sendIRC().quitServer();
            }
        }
    }

    @Override
    public long getId() {
        return id;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isLurk() {
        return lurk;
    }

    public void setLurk(boolean lurk) {
        this.lurk = lurk;
    }
}
