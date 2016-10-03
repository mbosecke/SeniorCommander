package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.IOException;

/**
 * To invite to server: https://discordapp.com/oauth2/authorize?&client_id=232338038250930177&scope=bot&permissions=257024
 * <p>
 * Created by mitch_000 on 2016-07-03.
 */
public class DiscordChannel implements Channel {

    Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    private volatile boolean running = true;

    private final long id;
    private final String token;
    private final String guildName;
    private final String channelName;

    private IDiscordClient discordClient;
    private IChannel channel;
    private MessageQueue messageQueue;

    public DiscordChannel(long id, String token, String guild, String channel) {
        this.id = id;
        this.token = token;
        this.guildName = guild;
        this.channelName = channel;
    }

    @Override
    public void listen(MessageQueue messageQueue) throws IOException {

        synchronized (startupLock) {
            if (running) {
                this.messageQueue = messageQueue;

                ClientBuilder clientBuilder = new ClientBuilder();
                clientBuilder.withToken(token);
                try {
                    discordClient = clientBuilder.login();
                    discordClient.getDispatcher().registerListener(this);

                } catch (DiscordException e) {
                    throw new RuntimeException(e);
                }
                logger.debug("Discord channel started");
            }
        }

    }

    @EventSubscriber
    public void onReady(ReadyEvent event){
        for (IChannel channel : discordClient.getChannels()) {
            logger.debug("Found discord channel: " + channel.getName());
            if (channel.getGuild().getName().equalsIgnoreCase(guildName) && channel.getName()
                    .equalsIgnoreCase(channelName)) {
                this.channel = channel;
                break;
            }
        }
    }

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        IGuild guild = message.getGuild();
        if(message.getGuild().getName().equalsIgnoreCase(guildName) && message.getChannel().getName().equalsIgnoreCase(channelName)) {
            logger.debug(String.format("Received message on discord server [%s]", guild.getName()));
            messageQueue.add(Message.userInput(this, message.getAuthor().getName(), null, message.getContent(), false));
        }
    }

    @Override
    public void sendMessage(String content) {
        if (running && channel != null) {
            try {
                channel.sendMessage(content);
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendMessage(String recipient, String content) {
        if (running && channel != null) {
            try {
                channel.sendMessage(String.format("@%s, %s", recipient, content));
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendWhisper(String recipient, String content) {
        // not yet supported
    }

    @Override
    public void timeout(String user, long duration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        synchronized (startupLock) {
            running = false;
            if (discordClient != null) {
                logger.debug("Shutting down discord channel.");
                try {
                    discordClient.logout();
                } catch (RateLimitException | DiscordException e) {
                    // TODO
                }
            }
        }
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public long getId() {
        return id;
    }
}
