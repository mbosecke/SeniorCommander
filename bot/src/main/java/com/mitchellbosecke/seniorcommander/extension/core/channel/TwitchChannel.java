package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.extension.core.CoreExtension;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.message.MessageUtils;
import com.mitchellbosecke.seniorcommander.utils.ExecutorUtils;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    private MessageQueue messageQueue;

    private PircBotX ircClient;

    private volatile boolean online = false;

    private final ExecutorService reconnectionExecutorService = Executors.newSingleThreadExecutor();

    private static final int RECONNECTION_RATE = 5 * 60 * 1000;

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
                connect();
            }
            estabishAutomaticReconnection();
        }
    }

    private void estabishAutomaticReconnection() {
        reconnectionExecutorService.submit(() -> {
            while (true) {
                synchronized (startupLock) {
                    if (running && ircClient != null && !ircClient.isConnected()) {
                        logger.debug("Reconnecting twitch channel");
                        connect();
                    }
                }
                Thread.sleep(RECONNECTION_RATE);
            }
        });
    }

    private void connect() throws IOException {
        /*
        We use a signal to determine when the rate limiter allows us to join.
        Unfortunately, we can't put the connection code directly in the runnable
        because this code blocks and would prevent other channels from connecting.
         */
        CountDownLatch signal = new CountDownLatch(1);
        CoreExtension.TWITCH_JOIN_RATE_LIMITER.submit(signal::countDown);
        try {
            signal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        org.pircbotx.Configuration configuration = new org.pircbotx.Configuration.Builder().setName(username)
                .setServerPassword(password).addServer(server, port).addListener(this).setAutoNickChange(false)
                .setOnJoinWhoEnabled(false).setCapEnabled(true)
                .addCapHandler(new EnableCapHandler("twitch.tv/commands"))
                .addCapHandler(new EnableCapHandler("twitch.tv/membership")).addAutoJoinChannel(channel.toLowerCase())
                .setAutoReconnect(false).buildConfiguration();

        ircClient = new PircBotX(configuration);

        logger.debug("Connecting to IRC [{}]", channel);
        try {
            ircClient.startBot();
        } catch (IrcException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void disconnect() {
        try {
            ircClient.stopBotReconnect();
            ircClient.sendIRC().quitServer();
            logger.debug("IRC channel disconnected");
        } catch (Exception ex) {
            logger.error("Exception occurred while shutting down IRC server", ex);
            // may throw an exception if the library has already
            // registered a shutdown hook and has stopped itself
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


        String[] split = MessageUtils.splitRecipient(event.getMessage());
        String recipient = split[0];
        String message = split[1];

        if (username.equalsIgnoreCase(recipient)) {
            recipient = SeniorCommander.getName();
        }

        String sender = event.getUser().getNick();

        logger.debug("Received message on IRC Channel [{}: {}] ", sender, message);

        messageQueue.add(Message.userInput(this, sender, recipient, message, false));
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
        String command = ircProtocolMessage.getCommand();
        switch(command){
            case "WHISPER":
                if (username.equalsIgnoreCase(ircProtocolMessage.getNick())) {
                    return;
                }
                logger.debug("Received whisper on IRC Channel [{}]", ircProtocolMessage.getLastParam());
                messageQueue.add(Message
                        .userInput(this, ircProtocolMessage.getNick(), SeniorCommander.getName(), ircProtocolMessage
                                .getLastParam(), true));
                break;
            case "CAP":
            case "ROOMSTATE":
            case "USERSTATE":
                break;
            default:
                logger.debug("Received unknown command [{}]", ircProtocolMessage.getCommand());
        }
    }

    @Override
    public void onNotice(NoticeEvent event) throws Exception {
        logger.trace("Notice event [{}] ", event.getNotice());
        messageQueue.add(Message.modList(this, event.getNotice()));
    }

    @Override
    public void onJoin(JoinEvent event) throws Exception {
        logger.trace("Join event [{}] ", event.getUser().getNick());
        messageQueue.add(Message.join(this, event.getUser().getNick()));
    }

    @Override
    public void onPart(PartEvent event) throws Exception {
        logger.trace("Part event [{}] ", event.getUser().getNick());
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
        logger.debug("User list [{}] ", names.toString());
    }

    public void getModList() {
        CoreExtension.TWITCH_MESSAGE_RATE_LIMITER.submit(() -> ircClient.sendIRC().message(channel, "/mods"));
    }

    @Override
    public void sendMessage(String content) {
        if (running) {
            CoreExtension.TWITCH_MESSAGE_RATE_LIMITER.submit(() -> ircClient.sendIRC().message(channel, content));
        }
    }

    @Override
    public void sendMessage(String recipient, String content) {
        if (running) {
            CoreExtension.TWITCH_MESSAGE_RATE_LIMITER
                    .submit(() -> ircClient.sendIRC().message(channel, "@" + recipient + ", " + content));
        }
    }

    @Override
    public void sendWhisper(String recipient, String content) {
        if (running) {
            CoreExtension.TWITCH_MESSAGE_RATE_LIMITER.submit(() -> ircClient.sendRaw()
                    .rawLine(String.format("PRIVMSG %s :/w %s %s", channel, recipient, content)));
        }
    }

    @Override
    public void timeout(String user, long duration) {
        if (running) {
            CoreExtension.TWITCH_MESSAGE_RATE_LIMITER.submit(() -> ircClient.sendIRC()
                    .message(channel, String.format(".timeout %s %d", user, duration)));
        }
    }

    @Override
    public void onDisconnect(DisconnectEvent event) throws Exception {
        super.onDisconnect(event);
        if (running) {
            logger.debug("Disconnected.");
        }
    }

    @Override
    public void onConnectAttemptFailed(ConnectAttemptFailedEvent event) throws Exception {
        super.onConnectAttemptFailed(event);
        if (running) {
            logger.debug("Connection attempt failed.");
        }
    }

    @Override
    public void shutdown() {
        synchronized (startupLock) {
            if (running) {
                running = false;
                logger.debug("Shutting down.");
                ExecutorUtils.shutdown(reconnectionExecutorService, 2, TimeUnit.SECONDS);
                disconnect();
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
    public boolean isCommunityOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String getBotUsername() {
        return username;
    }
}
