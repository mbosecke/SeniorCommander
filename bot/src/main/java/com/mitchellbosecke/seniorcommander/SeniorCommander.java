package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.IrcChannel;
import com.mitchellbosecke.seniorcommander.message.*;
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

        List<Channel> channels = buildChannels(configuration);
        List<MessageHandler> messageHandlers = buildMessageHandlers(messageQueue, channels);


        // each channel runs on it's own thread
        ExecutorService executor = Executors.newFixedThreadPool(channels.size());
        for (Channel channel : channels) {
            executor.submit(() -> {
                try {
                    channel.listen(messageQueue);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            Message message = messageQueue.readMessage();
            for (MessageHandler handler : messageHandlers) {
                handler.handle(message);
            }
        }


        for (Channel channel : channels) {
            channel.shutdown();
        }
        executor.shutdown();
    }

    private List<Channel> buildChannels(Configuration configuration) {
        List<Channel> channels = new ArrayList<>();
        channels.add(new IrcChannel(configuration));
        return channels;
    }

    private List<MessageHandler> buildMessageHandlers(MessageQueue messageQueue, List<Channel> channels) {
        List<MessageHandler> messageHandlers = new ArrayList<>();
        messageHandlers.add(new LoggingHandler());
        messageHandlers.add(new DiceHandler(messageQueue));

        for (Channel channel : channels) {
            messageHandlers.add(new OutputHandler(channel));
        }
        return messageHandlers;
    }

    public static void main(String[] args) throws IOException, IrcException {
        Configuration config = new Configuration("config.properties");
        new SeniorCommander(config);
    }
}
