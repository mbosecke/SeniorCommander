package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.IrcChannel;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.message.PrintMessageHandler;
import org.jibble.pircbot.IrcException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class SeniorCommander {


    public SeniorCommander(Configuration configuration) {

        MessageQueue messageQueue = new MessageQueue();

        List<Channel> channels = buildChannels();
        List<MessageHandler> messageHandlers = buildMessageHandlers();


        // each channel runs on it's own thread
        ExecutorService executor = Executors.newFixedThreadPool(channels.size());
        for (Channel channel : channels) {
            executor.submit(() -> {
                try {
                    channel.listen(configuration, messageQueue);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }


        for (Channel channel : channels) {
            channel.shutdown();
        }
        executor.shutdown();
    }

    private List<Channel> buildChannels() {
        List<Channel> channels = new ArrayList<>();
        channels.add(new IrcChannel());
        return channels;
    }

    private List<MessageHandler> buildMessageHandlers() {
        List<MessageHandler> messageHandlers = new ArrayList<>();
        messageHandlers.add(new PrintMessageHandler());
        return messageHandlers;
    }

    public static void main(String[] args) throws IOException, IrcException {
        Configuration config = new Configuration("config.properties");
        new SeniorCommander(config);
    }
}
