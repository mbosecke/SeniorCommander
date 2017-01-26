package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.CoreExtension;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
import com.mitchellbosecke.seniorcommander.utils.DatabaseManager;
import com.mitchellbosecke.seniorcommander.utils.ExecutorUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    private final ExecutorService channelThreadPool;

    /**
     * A boolean used to determine if the bot should be running or not. If set to false, the polling of the message
     * queue will cease and a shutdown procedure will begin.
     */
    private volatile boolean running = true;

    /**
     * Global session factory
     */
    private final SessionFactory sessionFactory;

    /**
     * Global message queue
     */
    private final MessageQueue messageQueue;

    private final TimerManager timerManager;

    /**
     * Components created from the extensions
     */
    private List<Extension> extensions = new ArrayList<>();
    private List<Channel> channels = new LinkedList<>();
    private List<EventHandler> eventHandlers = new LinkedList<>();
    private List<CommandHandler> commandHandlers = new LinkedList<>();

    public SeniorCommanderImpl() {
        this(Collections.emptyList());
    }

    public SeniorCommanderImpl(List<Extension> extensions) {

        // initiate session factory
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.migrate();
        sessionFactory = databaseManager.getSessionFactory();

        // message queue
        messageQueue = new MessageQueue();

        // timer manager
        timerManager = new TimerManager(Executors.newScheduledThreadPool(5), sessionFactory);

        // add core extension to list of user-provided extensions
        registerExtensions(extensions);

        // each channel runs on it's own thread
        channelThreadPool = Executors.newFixedThreadPool(channels.size());

        // setup a shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this, Thread.currentThread()));
    }

    /**
     * Runs each channel on a child thread and then blocks while waiting for new messages on the queue.
     * Can be interrupted with {@link #shutdown()}.
     */
    @Override
    public void run() {

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

        while (true) {
            Message message = messageQueue.readMessage(); // only blocks for a small period of time
            if (message != null) {
                logger.trace("Message from queue: [" + message.getType() + ": " + message.getContent() + "]");
                Session session = sessionFactory.getCurrentSession();
                session.beginTransaction();
                try {

                    logger.debug("Primary transaction beginning [{}]", session.getTransaction().getStatus());
                    eventHandlers.forEach(eventHandler -> {
                        try {
                            eventHandler.handle(message);
                        } catch (Exception ex) {
                            // we don't want to die! Just log the error.
                            logger.error("Error when handling message", ex);
                        }
                    });
                    logger.debug("Committing primary transaction");
                    session.getTransaction().commit();
                } catch (Exception ex) {
                    logger.error("Rolling back primary transaction");
                    session.getTransaction().rollback();

                    throw ex;
                } finally {
                    session.close();
                }
            }
            if (!running) {
                break;
            }
        }
    }

    @Override
    public void shutdown() {
        logger.debug("Shutting down SeniorCommander.");
        running = false;

        timerManager.shutdown();
        extensions.forEach(extension -> extension.onShutdown(sessionFactory));
        channels.forEach(Channel::shutdown);
        ExecutorUtils.shutdown(channelThreadPool, 10, TimeUnit.SECONDS);
        sessionFactory.close();
    }

    private void registerExtensions(List<Extension> extensions) {
        List<Extension> allExtensions = new ArrayList<>();
        allExtensions.add(new CoreExtension());
        allExtensions.addAll(extensions);

        // build components from extensions
        buildChannels(allExtensions);
        buildCommandHandlers(allExtensions);
        buildEventHandlers(allExtensions);
        startTimers(allExtensions);
        this.extensions = allExtensions;
    }

    private void buildChannels(List<Extension> extensions) {

        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            for (Extension extension : extensions) {

                List<ChannelFactory> channelFactories = extension.getChannelFactories();
                if (channelFactories != null) {
                    channelFactories.forEach(channelFactory -> channels.addAll(channelFactory.build(session)));
                }
            }
            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
            throw ex;
        } finally {
            session.close();
        }
    }

    private void startTimers(List<Extension> extensions) {
        Session session = sessionFactory.getCurrentSession();
        try {
           session.beginTransaction();

            for (Extension extension : extensions) {
                extension.startTimers(sessionFactory, messageQueue, channels, timerManager);
            }
            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
            throw ex;
        } finally {
            session.close();
        }
    }

    private void buildCommandHandlers(List<Extension> extensions) {
        for (Extension extension : extensions) {
            commandHandlers.addAll(extension.buildCommandHandlers(sessionFactory, messageQueue, timerManager));
        }
    }

    private void buildEventHandlers(List<Extension> extensions) {
        for (Extension extension : extensions) {
            eventHandlers.addAll(extension.buildEventHandlers(sessionFactory, messageQueue, channels, commandHandlers));
        }
    }


    private static class ShutdownHook extends Thread {
        private final WeakReference<SeniorCommander> botRef;
        private final Thread mainThread;

        public ShutdownHook(SeniorCommander bot, Thread mainThread){
            this.botRef = new WeakReference<>(bot);
            this.mainThread = mainThread;
        }

        @Override
        public void run() {
            SeniorCommander bot = botRef.get();
            if(bot != null){
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
