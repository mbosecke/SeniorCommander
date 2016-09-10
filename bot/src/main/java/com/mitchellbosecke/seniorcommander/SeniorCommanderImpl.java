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

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class SeniorCommanderImpl implements SeniorCommander {

    Logger logger = LoggerFactory.getLogger(getClass());

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
        timerManager = new TimerManager(Executors.newScheduledThreadPool(5));

        // add core extension to list of user-provided extensions
        registerExtensions(extensions);

        // each channel runs on it's own thread
        channelThreadPool = Executors.newFixedThreadPool(channels.size());
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

                Session session = sessionFactory.getCurrentSession();
                session.beginTransaction();

                eventHandlers.forEach(eventHandler -> {
                    try {
                        eventHandler.handle(message);
                    } catch (Exception ex) {
                        // we don't want to die! Just log the error.
                        logger.error("Error when handling message", ex);
                    }
                });

                session.getTransaction().commit();
                session.close();
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
        channels.forEach(Channel::shutdown);
        timerManager.shutdown();
        ExecutorUtils.shutdown(channelThreadPool, 10, TimeUnit.SECONDS);
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
    }

    private void buildChannels(List<Extension> extensions) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        for (Extension extension : extensions) {

            List<ChannelFactory> channelFactories = extension.getChannelFactories();
            if (channelFactories != null) {
                channelFactories.forEach(channelFactory -> {
                    channels.addAll(channelFactory.build(session));
                });
            }
        }

        session.getTransaction().commit();
        session.close();
    }

    private void startTimers(List<Extension> extensions) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        for (Extension extension : extensions) {
            extension.startTimers(session, messageQueue, channels, timerManager);
        }
        session.getTransaction().commit();
        session.close();
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

}
