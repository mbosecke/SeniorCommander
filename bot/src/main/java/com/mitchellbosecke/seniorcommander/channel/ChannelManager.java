package com.mitchellbosecke.seniorcommander.channel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mitch_000 on 2017-01-28.
 */
public class ChannelManager {

    /**
     * All the channels and their known state.
     */
    private final Map<Channel, State> channels;

    private final ExecutorService executorService;

    private final ExecutorService monitoringExecutorService = Executors
            .newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("channel-monitor-%d").build());

    private final AtomicInteger numberOfRunningChannels = new AtomicInteger(0);

    private final MessageQueue messageQueue;

    private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);

    private static final int MONITOR_RATE = 1 * 1000;

    private final List<Channel> channelsToBeStopped = new ArrayList<>();

    private volatile boolean monitoring = true;

    /**
     * Starting and stopping channels is synchronized because it can be called from
     * many threads and we want to carefully track which channels are SUPPOSED to be
     * running, to better support to auto-reconnection feature.
     */
    private static final Object lock = new Object();

    public ChannelManager(List<Channel> channels, ExecutorService executorService, MessageQueue messageQueue) {

        this.channels = new HashMap<>();
        channels.forEach(c -> this.channels.put(c, State.STOPPED));

        this.executorService = executorService;
        this.messageQueue = messageQueue;

        channelMonitoring();
    }

    public void startAllChannels() {
        synchronized (lock) {
            channels.forEach((channel, running) -> startChannel(channel));
        }
    }

    public void startChannel(Channel channel) {
        synchronized (lock) {
            State state = channels.get(channel);
            if (state == State.STOPPED || (state == State.STARTED && !channel.isListening())) {

                logger.debug("Starting channel [{}]", channel);
                channels.put(channel, State.STARTING_UP);
                numberOfRunningChannels.incrementAndGet();
                executorService.submit(() -> {
                    try {
                        channel.listen(messageQueue);
                    } catch (Exception e) {
                        logger.error("Exception in channel", e);
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    /**
     * There are a few things we are looking for.
     * <p>
     * First, channels that were known to be starting up and are now fully listening.
     * However, it's possible that this channel has since been flagged to be stopped
     * so we will actually initiate the shutdown procedure for it at this point.
     * <p>
     * Secondly, channels that were known to be
     * started but for some unexpected reason they've stopped listening (and should be
     * reconnected).
     */
    public void channelMonitoring() {
        monitoringExecutorService.submit(() -> {
            while (monitoring) {

                logger.trace("Channel monitoring engaged");
                synchronized (lock) {
                    channels.forEach((channel, state) -> {
                        if (state == State.STARTING_UP && channel.isListening()) {

                            logger.debug("A channel starting up has fully started [{}]", channel);
                            channels.put(channel, State.STARTED);
                            if (channelsToBeStopped.contains(channel)) {
                                logger.debug("A channel starting up has fully started but needs to be stopped [{}]", channel);
                                stopChannel(channel);
                            }
                            // track when channels have fully started
                        } else if (state == State.STARTED && !channel.isListening()) {

                            // channel must have unexpectedly disconnected itself
                            logger.debug("Reconnecting channel [{}]", channel);
                            startChannel(channel);
                        }
                    });
                }

                try {
                    Thread.sleep(MONITOR_RATE);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void stopChannel(Channel channel) {
        synchronized (lock) {
            State currentState = channels.get(channel);
            if (currentState == State.STARTED) {
                logger.debug("Stopping channel [{}]", channel);
                channels.put(channel, State.STOPPED);
                channel.shutdown();
                numberOfRunningChannels.decrementAndGet();

                if (channelsToBeStopped.contains(channel)) {
                    channelsToBeStopped.remove(channel);
                }

            } else if (currentState == State.STARTING_UP) {
                logger.debug("Channel is just starting up, flagging it to be stopped soon.");
                // Let's let the channel fully start up
                // and leave it to the monitor to
                // shut it down afterwards.
                channelsToBeStopped.add(channel);
            } else {
                logger.debug("Unable to stop channel, it is in an unknown state [{}]", currentState);
            }

        }
    }

    public Set<Channel> getChannels() {
        return Collections.unmodifiableSet(channels.keySet());
    }

    public void shutdown() {
        synchronized (lock) {
            channels.forEach((channel, running) -> {
                stopChannel(channel);
            });
        }

        while (numberOfRunningChannels.get() > 0) {
            logger.debug("Waiting for " + numberOfRunningChannels.get() + " channels to shutdown");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        monitoring = false;
        ExecutorUtils.shutdown(monitoringExecutorService, 5, TimeUnit.SECONDS);
        ExecutorUtils.shutdown(executorService, 10, TimeUnit.SECONDS);

    }

    private enum State {
        STOPPED, STARTED, STARTING_UP
    }
}
