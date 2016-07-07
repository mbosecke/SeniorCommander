package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.extension.CoreExtension;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.scheduled.ScheduledTask;
import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class SeniorCommander {

    Logger logger = LoggerFactory.getLogger(getClass());

    public SeniorCommander(Configuration configuration, List<Extension> extensions) {

        Context context = buildContext(configuration, extensions);

        // each channel runs on it's own thread
        ExecutorService executor = Executors.newFixedThreadPool(context.getChannels().size());
        for (Channel channel : context.getChannels()) {
            executor.submit(() -> {
                try {
                    channel.listen(context);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // initiate the scheduled tasks
        for (ScheduledTask scheduledTask : context.getScheduledTasks()) {
            scheduledTask.initiate(context);
        }

        for (int i = 0; i < 10; i++) {
            Message message = context.getMessageQueue().readMessage();
            for (MessageHandler handler : context.getMessageHandlers()) {
                try {
                    handler.handle(context, message);
                } catch (Exception ex) {
                    // we don't want to die! Just log the error.
                    logger.error("Error when handling message", ex);
                }
            }
        }

        for (Channel channel : context.getChannels()) {
            channel.shutdown();
        }
        executor.shutdown();
    }

    private Context buildContext(Configuration configuration, List<Extension> extensions) {
        List<Channel> channels = new ArrayList<>();
        List<MessageHandler> handlers = new ArrayList<>();
        List<ScheduledTask> scheduledTasks = new ArrayList<>();
        for (Extension extension : extensions) {
            channels.addAll(extension.getChannels());
            handlers.addAll(extension.getMessageHandlers());
            scheduledTasks.addAll(extension.getScheduledTasks());
        }
        return new Context(configuration, new MessageQueue(), channels, handlers, scheduledTasks, Executors
                .newScheduledThreadPool(10));
    }

    public static void main(String[] args) throws IOException, IrcException {
        Configuration config = new Configuration("config.properties");
        Extension extension = new CoreExtension();
        new SeniorCommander(config, Collections.singletonList(extension));
    }
}
