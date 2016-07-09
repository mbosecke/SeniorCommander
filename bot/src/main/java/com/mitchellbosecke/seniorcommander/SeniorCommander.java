package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.extension.CoreExtension;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class SeniorCommander {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final Context context;

    private final ExecutorService executorService;

    private volatile boolean running = true;

    private List<MessageHandler> newlyRegisteredHandlers = new LinkedList<>();

    public SeniorCommander(Configuration configuration, List<Extension> extensions) {

        List<Extension> allExtensions = new ArrayList<>();
        allExtensions.add(new CoreExtension()); // core extension is mandatory
        allExtensions.addAll(extensions);
        context = buildContext(configuration, allExtensions);

        // each channel runs on it's own thread
        executorService = Executors.newFixedThreadPool(context.getChannels().size());

    }

    /**
     * Runs each channel on a child thread and then blocks while waiting for new messages on the queue.
     * Can be interrupted with {@link #shutdown()}.
     */
    public void run() {
        // run each channel on it's own thread
        for (Channel channel : context.getChannels()) {
            executorService.submit(() -> {
                try {
                    channel.listen(context);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // run the timer tasks
        context.getTimers().forEach(timer -> timer.run(context));

        while (true) {
            Message message = context.getMessageQueue().readMessage();
            if (message != null) {

                context.getMessageHandlers().forEach(messageHandler -> {
                    try {
                        messageHandler.handle(context, message);
                    } catch (Exception ex) {
                        // we don't want to die! Just log the error.
                        logger.error("Error when handling message", ex);
                    }
                });
            }
            acknowledgeNewHandlers();
            if (!running) {
                break;
            }
        }
    }

    public void shutdown() {
        logger.debug("Shutting down SeniorCommander.");
        running = false;
        context.getChannels().forEach(Channel::shutdown);
        ExecutorUtils.shutdown(executorService, 10, TimeUnit.SECONDS);
    }

    public void registerHandler(MessageHandler messageHandler) {
        newlyRegisteredHandlers.add(messageHandler);
    }

    private void acknowledgeNewHandlers() {
        context.getMessageHandlers().addAll(newlyRegisteredHandlers);
        newlyRegisteredHandlers.clear();
    }

    /**
     * Name used to populate the "sender" and "recipient" fields on a message.
     *
     * @return
     */
    public static String getName() {
        return SeniorCommander.class.getName();
    }

    private Context buildContext(Configuration configuration, List<Extension> extensions) {
        List<Channel> channels = new ArrayList<>();
        List<MessageHandler> handlers = new LinkedList<>();
        List<Timer> timers = new ArrayList<>();
        for (Extension extension : extensions) {
            channels.addAll(extension.getChannels());
            handlers.addAll(extension.getMessageHandlers());
            timers.addAll(extension.getTimers());
        }
        return new Context(this, configuration, new MessageQueue(), channels, handlers, timers, Executors
                .newScheduledThreadPool(10));
    }

    public static void main(String[] args) throws IOException, IrcException {
        Configuration config = new Configuration("config.properties");
        SeniorCommander commander = new SeniorCommander(config, Collections.emptyList());
        commander.run();
    }
}
