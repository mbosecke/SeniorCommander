package com.mitchellbosecke.seniorcommander.channel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.HttpChannel;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.ConfigUtils;
import com.mitchellbosecke.seniorcommander.utils.ExecutorUtils;
import com.mitchellbosecke.seniorcommander.utils.NetworkUtils;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;
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
     * All the channelStates and their known state.
     */
    private final List<ChannelFactory> channelFactories;

    private final Map<Channel, State> channelStates;

    private final Map<Long, Channel> channels;

    private ExecutorService executorService;

    private final ExecutorService monitoringExecutorService = Executors
            .newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("channel-monitor-%d").build());

    private final AtomicInteger numberOfRunningChannels = new AtomicInteger(0);

    private final MessageQueue messageQueue;

    private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);

    private static final int MONITOR_RATE = 1 * 1000;

    private final List<Channel> channelsToBeStopped = new ArrayList<>();

    private volatile boolean monitoring = true;

    /**
     * Starting and stopping channelStates is synchronized because it can be called from
     * many threads and we want to carefully track which channelStates are SUPPOSED to be
     * running, to better support to auto-reconnection feature.
     */
    private static final Object lock = new Object();

    public ChannelManager(List<ChannelFactory> channelFactories, MessageQueue messageQueue) {

        this.channelFactories = channelFactories;
        this.messageQueue = messageQueue;
        this.channelStates = new HashMap<>();
        this.channels = new HashMap<>();

        channelMonitoring();
    }

    public void start() {
        synchronized (lock) {

            List<ChannelModel> channelModels = new ArrayList<>();
            TransactionManager.runInTransaction(session -> {

                //@formatter:off
                channelModels.addAll(session
                        .createQuery("" +
                                "SELECT DISTINCT cm " +
                                "FROM ChannelModel cm " +
                                "LEFT JOIN FETCH cm.settings " +
                                "WHERE cm.communityModel.server = :server", ChannelModel.class)
                        .setParameter("server",  NetworkUtils.getLocalHostname())
                        .getResultList());
                //@formatter:on


                List<Channel> channels = new ArrayList<>();
                for (ChannelModel channelModel : channelModels) {
                    channels.add(buildChannel(channelModel));
                }

                channels.add(new HttpChannel(ConfigUtils.getInt("http.port"), channels));

                for (Channel channel : channels) {
                    this.channelStates.put(channel, State.STOPPED);
                    this.channels.put(channel.getId(), channel);
                }

                this.executorService = Executors.newFixedThreadPool(this.channels.size(), new ThreadFactoryBuilder()
                        .setNameFormat("channels-%d").build());
            });

            this.channels.values().forEach(channel -> startChannel(channel, false));
        }
    }

    /**
     *
     * @param channel
     * @param fullRefresh If doing a full refresh it will query the database for the latest settings
     */
    public void startChannel(Channel channel, boolean fullRefresh) {
        synchronized (lock) {
            State state = channelStates.get(channel);
            if (state == State.STOPPED || (state == State.STARTED && !channel.isListening())) {

                TransactionManager.runInTransaction(session -> {
                    logger.debug("Starting channel [{}]", channel);

                    Channel newChannel;

                        if(fullRefresh && !(channel instanceof  HttpChannel)) {
                            //@formatter:off
                            ChannelModel channelModel = session
                                .createQuery("" +
                                        "SELECT cm " +
                                        "FROM ChannelModel cm " +
                                        "LEFT JOIN FETCH cm.settings " +
                                        "WHERE cm.id = :id", ChannelModel.class)
                                .setParameter("id",  channel.getId())
                                .getSingleResult();
                            //@formatter:on

                            newChannel = buildChannel(channelModel);

                            // remove old data
                            channelStates.remove(channel);
                            channels.remove(channel.getId());

                            // add new data
                            channelStates.put(newChannel, State.STARTING_UP);
                            channels.put(newChannel.getId(), newChannel);

                        }else{
                            newChannel = channel;
                        }

                    numberOfRunningChannels.incrementAndGet();
                    executorService.submit(() -> {
                        try {
                            newChannel.listen(messageQueue);
                        } catch (Exception e) {
                            logger.error("Exception in channel", e);
                            throw new RuntimeException(e);
                        }
                    });
                });

            }
        }
    }

    private Channel buildChannel(ChannelModel channelModel) {
        Channel result = null;
        for (ChannelFactory factory : channelFactories) {
            if (factory.supports(channelModel.getType())) {
                result = factory.build(channelModel);
                break;
            }
        }
        return result;
    }

    /**
     * There are a few things we are looking for.
     * <p>
     * First, channelStates that were known to be starting up and are now fully listening.
     * However, it's possible that this channel has since been flagged to be stopped
     * so we will actually initiate the shutdown procedure for it at this point.
     * <p>
     * Secondly, channelStates that were known to be
     * started but for some unexpected reason they've stopped listening (and should be
     * reconnected).
     */
    public void channelMonitoring() {
        monitoringExecutorService.submit(() -> {
            while (monitoring) {

                logger.trace("Channel monitoring engaged");
                synchronized (lock) {
                    channelStates.forEach((channel, state) -> {
                        if (state == State.STARTING_UP && channel.isListening()) {

                            logger.debug("A channel starting up has fully started [{}]", channel);
                            channelStates.put(channel, State.STARTED);
                            if (channelsToBeStopped.contains(channel)) {
                                logger.debug("A channel starting up has fully started but needs to be stopped [{}]", channel);
                                stopChannel(channel);
                            }
                            // track when channelStates have fully started
                        } else if (state == State.STARTED && !channel.isListening()) {

                            // channel must have unexpectedly disconnected itself
                            logger.debug("Reconnecting channel [{}]", channel);
                            startChannel(channel, false);
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
            State currentState = channelStates.get(channel);
            if (currentState == State.STARTED) {
                logger.debug("Stopping channel [{}]", channel);
                channelStates.put(channel, State.STOPPED);
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

    public Set<Channel> getChannelStates() {
        return Collections.unmodifiableSet(channelStates.keySet());
    }

    public Optional<Channel> getChannel(long id) {
        return Optional.ofNullable(channels.get(id));
    }

    public void shutdown() {
        synchronized (lock) {
            channelStates.forEach((channel, running) -> {
                stopChannel(channel);
            });
        }

        while (numberOfRunningChannels.get() > 0) {
            logger.debug("Waiting for " + numberOfRunningChannels.get() + " channelStates to shutdown");

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
