package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.CoreExtension;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
import com.mitchellbosecke.seniorcommander.utils.DatabaseManager;
import com.mitchellbosecke.seniorcommander.utils.ExecutorUtils;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class SeniorCommanderImpl implements SeniorCommander {

    private static final Logger logger = LoggerFactory.getLogger(SeniorCommanderImpl.class);

    /**
     * Executor service used to run the individual channels
     */
    private ExecutorService channelThreadPool;

    /**
     * A boolean used to determine if the bot should be running or not. If set to false, the polling of the message
     * queue will cease and a shutdown procedure will begin.
     */
    private volatile boolean running = true;

    private final Object startupLock = new Object();

    /**
     * Global message queue
     */
    private final MessageQueue messageQueue;

    private final TimerManager timerManager;

    /**
     * Components created from the extensions
     */
    private final List<Extension> extensions;
    private List<Channel> channels = new LinkedList<>();
    private List<EventHandler> eventHandlers = new LinkedList<>();
    private List<CommandHandler> commandHandlers = new LinkedList<>();

    public SeniorCommanderImpl() {
        this(Collections.emptyList());
    }

    public SeniorCommanderImpl(List<Extension> extensions) {

        // migrate database and create transaction manager
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.migrate();
        TransactionManager.initiate(databaseManager.getSessionFactory());

        // message queue
        messageQueue = new MessageQueue();

        // timer manager
        timerManager = new TimerManager(Executors.newScheduledThreadPool(5));

        // add core extension to list of user-provided extensions
        // extension registry
        List<Extension> allExtensions = new ArrayList<>();
        allExtensions.add(new CoreExtension());
        allExtensions.addAll(extensions);
        this.extensions = allExtensions;

        // setup a shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this, Thread.currentThread()));
    }

    /**
     * Runs each channel on a child thread and then blocks while waiting for new messages on the queue.
     * Can be interrupted with {@link #shutdown()}.
     */
    @Override
    public void run() {

        synchronized (startupLock) {
            if (running) {

                // build components from each extension
                initExtensions();

                // each channel runs on it's own thread
                channelThreadPool = Executors.newFixedThreadPool(channels.size());

                // run each channel on it's own thread
                for (Channel channel : channels) {
                    channelThreadPool.submit(() -> {
                        try {
                            channel.listen(messageQueue);
                        } catch (Exception e) {
                            logger.debug("Exception in channel: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        }

        while (running) {
            Message message = messageQueue.readMessage(); // only blocks for a small period of time

            synchronized (startupLock) {
                if (message != null && running) {
                    logger.trace("Message from queue: [" + message.getType() + ": " + message.getContent() + "]");

                    TransactionManager.runInTransaction(session -> {
                        logger.debug("Primary transaction beginning");
                        eventHandlers.forEach(eventHandler -> {
                            try {
                                eventHandler.handle(message);
                            } catch (Exception ex) {
                                // we don't want to die! Just log the error.
                                logger.error("Error when handling message", ex);
                            }
                        });
                        logger.debug("Committing primary transaction");
                    });
                }
            }

        }
    }

    @Override
    public void shutdown() {
        logger.debug("Shutting down SeniorCommander.");
        synchronized (startupLock) {
            if (running) {
                running = false;
                timerManager.shutdown();
                extensions.forEach(Extension::onShutdown);
                channels.forEach(Channel::shutdown);
                ExecutorUtils.shutdown(channelThreadPool, 10, TimeUnit.SECONDS);
                TransactionManager.shutdown();
            }
        }
    }

    private void initExtensions() {

        TransactionManager.runInTransaction(session -> {
            for (Extension extension : extensions) {

                // build channels
                channels.addAll(extension.buildChannels());

                // start timers
                extension.buildTimers(messageQueue, channels).forEach(timerManager::addTimer);

                // command handlers
                commandHandlers.addAll(extension.buildCommandHandlers(messageQueue, timerManager));

                // event handlers
                eventHandlers.addAll(extension.buildEventHandlers(messageQueue, channels, commandHandlers));
            }
        });
    }

    private static class ShutdownHook extends Thread {
        private final WeakReference<SeniorCommander> botRef;
        private final Thread mainThread;

        ShutdownHook(SeniorCommander bot, Thread mainThread) {
            this.botRef = new WeakReference<>(bot);
            this.mainThread = mainThread;
        }

        @Override
        public void run() {
            SeniorCommander bot = botRef.get();
            if (bot != null) {
                bot.shutdown();
            }
            try {
                // wait for the main thread to finish
                mainThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
