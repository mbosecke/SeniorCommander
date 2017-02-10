package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.ChannelManager;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.CoreExtension;
import com.mitchellbosecke.seniorcommander.extension.core.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
import com.mitchellbosecke.seniorcommander.utils.DatabaseManager;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class SeniorCommanderImpl implements SeniorCommander {

    private static final Logger logger = LoggerFactory.getLogger(SeniorCommanderImpl.class);

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

    private ChannelManager channelManager;

    /**
     * Components created from the extensions
     */
    private final List<Extension> extensions;
    private List<ChannelFactory> channelFactories = new LinkedList<>();
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
    }

    /**
     * Runs each channel on a child thread and then blocks while waiting for new messages on the queue.
     * Can be interrupted with {@link #shutdown()}.
     */
    @Override
    public void run() {

        // setup a shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this, Thread.currentThread()));

        synchronized (startupLock) {
            if (running) {

                // build components from each extension
                initExtensions();

                // start channels
                channelManager = new ChannelManager(channelFactories, messageQueue);

                channelManager.start();
            }
        }

        while (running) {
            Message message = messageQueue.readMessage(); // only blocks for a small period of time

            synchronized (startupLock) {
                if (message != null && running) {
                    logger.trace("Message from queue: [" + message.getType() + ": " + message.getContent() + "]");

                    try {
                        TransactionManager.runInTransaction(session -> {
                            logger.debug("Primary transaction beginning");
                            eventHandlers.forEach(eventHandler -> {
                                eventHandler.handle(message);
                            });
                            logger.debug("Committing primary transaction");
                        });
                    }catch (Exception ex){
                        // don't die, just log the exception
                        logger.debug("An exception occurred while handling a message", ex);

                    }
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
                channelManager.shutdown();
                TransactionManager.shutdown();
                logger.debug("SeniorCommander has been shut down.");
            }else{
                logger.debug("Actually, it's not running");
            }
        }
    }

    private void initExtensions() {

        TransactionManager.runInTransaction(session -> {
            for (Extension extension : extensions) {

                // build channels
                channelFactories.addAll(extension.buildChannelFactories());

                // start timers
                extension.buildTimers(this).forEach(timerManager::addTimer);

                // command handlers
                commandHandlers.addAll(extension.buildCommandHandlers(this));

                // event handlers
                eventHandlers.addAll(extension.buildEventHandlers(this));
            }
        });
    }

    @Override
    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    @Override
    public ChannelManager getChannelManager() {
        return channelManager;
    }

    @Override
    public List<EventHandler> getEventHandlers() {
        return Collections.unmodifiableList(eventHandlers);
    }

    @Override
    public List<CommandHandler> getCommandHandlers() {
        return Collections.unmodifiableList(commandHandlers);
    }

    @Override
    public TimerManager getTimerManager() {
        return timerManager;
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
                logger.debug("Shutting down the bot due to VM dying");
                bot.shutdown();
            }
            try {
                // wait for the main thread to finish
                mainThread.join(20000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
