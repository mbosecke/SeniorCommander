package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.CoreExtension;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.handler.MessageHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
     * Global configuration
     */
    private final Configuration configuration;

    /**
     * Global session factory
     */
    private final SessionFactory sessionFactory;

    /**
     * Global message queue
     */
    private final MessageQueue messageQueue;

    /**
     * Components created from the extensions
     */
    private List<Channel> channels = new LinkedList<>();
    private List<MessageHandler> messageHandlers = new LinkedList<>();
    private List<Timer> timers = new LinkedList<>();

    /**
     * Message handlers added at runtime
     */
    private List<MessageHandler> newlyRegisteredHandlers = new LinkedList<>();

    public SeniorCommanderImpl(Configuration configuration, List<Extension> extensions) {

        this.configuration = configuration;

        // initiate session factory
        DatabaseManager databaseManager = new DatabaseManager(configuration);
        databaseManager.migrate();
        sessionFactory = databaseManager.getSessionFactory();

        // message queue
        messageQueue = new MessageQueue();

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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // run the timers
        timers.forEach(timer -> timer.run());

        while (true) {
            Message message = messageQueue.readMessage();
            if (message != null) {

                Session session = sessionFactory.getCurrentSession();
                session.beginTransaction();

                messageHandlers.forEach(messageHandler -> {
                    try {
                        messageHandler.handle(message);
                    } catch (Exception ex) {
                        // we don't want to die! Just log the error.
                        logger.error("Error when handling message", ex);
                    }
                });

                session.getTransaction().commit();
                session.close();
            }
            acknowledgeNewHandlers();
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
        ExecutorUtils.shutdown(channelThreadPool, 10, TimeUnit.SECONDS);
    }

    public void registerHandler(MessageHandler messageHandler) {
        newlyRegisteredHandlers.add(messageHandler);
    }

    private void acknowledgeNewHandlers() {
        messageHandlers.addAll(newlyRegisteredHandlers);
        newlyRegisteredHandlers.clear();
    }

    private void registerExtensions(List<Extension> extensions) {
        List<Extension> allExtensions = new ArrayList<>();
        allExtensions.add(new CoreExtension());
        allExtensions.addAll(extensions);

        // build components from extensions
        buildChannels(allExtensions);
        buildMessageHandlers(allExtensions);
        buildTimers(allExtensions);
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

    private void buildMessageHandlers(List<Extension> extensions) {
        for (Extension extension : extensions) {
            messageHandlers.addAll(extension.getMessageHandlerFactory()
                    .build(sessionFactory, messageQueue, configuration, channels));
        }
    }

    private void buildTimers(List<Extension> extensions) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        for (Extension extension : extensions) {
            timers.addAll(extension.getTimerFactory().build(scheduledExecutorService, messageQueue));
        }

    }

}
