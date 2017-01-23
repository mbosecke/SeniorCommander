package com.mitchellbosecke.seniorcommander.extension.core;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.channel.DiscordChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.SocketChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.command.*;
import com.mitchellbosecke.seniorcommander.extension.core.event.*;
import com.mitchellbosecke.seniorcommander.extension.core.service.*;
import com.mitchellbosecke.seniorcommander.extension.core.timer.*;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
import com.mitchellbosecke.seniorcommander.utils.RateLimiter;
import com.typesafe.config.ConfigFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class CoreExtension implements Extension {

    /**
     * The rate limiter used to join/auth with twitch IRC. It's normally 50 joins per 15 seconds but this
     * includes "AUTHS" which we do for every join so we can only perform 25 per 15 seconds.
     */
    public static final RateLimiter TWITCH_JOIN_RATE_LIMITER = new RateLimiter(25, 15);

    /**
     * The rate limiter used to send messages on Twitch IRC.
     */
    public static final RateLimiter TWITCH_MESSAGE_RATE_LIMITER = new RateLimiter(20, 30);

    private static final Logger logger = LoggerFactory.getLogger(CoreExtension.class);

    @Override
    public List<ChannelFactory> getChannelFactories() {
        List<ChannelFactory> factories = new ArrayList<>();
        factories.add(new TwitchChannelFactory());
        factories.add(new SocketChannelFactory());
        factories.add(new DiscordChannelFactory());
        return factories;
    }

    @Override
    public void startTimers(SessionFactory sessionFactory, MessageQueue messageQueue, List<Channel> channels,
                            TimerManager timerManager) {

        UserService userService = new UserService(sessionFactory);

        new ShoutTimerFactory(sessionFactory, channels, messageQueue).build().forEach(timerManager::addTimer);
        new PointTimerFactory(sessionFactory, channels, userService).build().forEach(timerManager::addTimer);
        new TwitchOnlineCheckerFactory(sessionFactory, channels).build().forEach(timerManager::addTimer);
        new FollowerAuditFactory(sessionFactory, channels, userService).build().forEach(timerManager::addTimer);
        new ModAuditFactory(sessionFactory, channels, userService).build().forEach(timerManager::addTimer);
    }

    @Override
    public List<EventHandler> buildEventHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                                 List<Channel> channels, List<CommandHandler> commandHandlers) {

        List<EventHandler> eventHandlers = new ArrayList<>();

        // service tiers
        UserService userService = new UserService(sessionFactory);
        CommandService commandService = new CommandService(sessionFactory);
        ChannelService channelService = new ChannelService(sessionFactory);
        GiveawayService giveawayService = new GiveawayService(sessionFactory);

        // handlers
        eventHandlers.add(new LoggingHandler(userService));
        eventHandlers.add(new OutputHandler(channels, channelService, userService));
        eventHandlers.add(new ConversationalHandler(messageQueue));
        eventHandlers.add(new UserChatHandler(userService));
        eventHandlers.add(new JoinPartHandler(userService));
        eventHandlers.add(new NamesHandler(userService));
        eventHandlers.add(new CommandBroker(messageQueue, commandHandlers, userService, commandService));
        eventHandlers.add(new ModListHandler(userService));
        eventHandlers.add(new GiveawayKeywordHandler(giveawayService, userService));

        return eventHandlers;
    }

    @Override
    public List<CommandHandler> buildCommandHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                                     TimerManager timerManager) {

        // service tiers
        CommandService commandService = new CommandService(sessionFactory);
        QuoteService quoteService = new QuoteService(sessionFactory);
        TimerService timerService = new TimerService(sessionFactory);
        UserService userService = new UserService(sessionFactory);
        BettingService bettingService = new BettingService(sessionFactory);
        GiveawayService giveawayService = new GiveawayService(sessionFactory);
        AuctionService auctionService = new AuctionService(sessionFactory);

        // handlers
        List<CommandHandler> commandHandlers = new ArrayList<>();
        commandHandlers.add(new Roll(messageQueue));
        commandHandlers.add(new Advice(messageQueue));
        commandHandlers.add(new Roulette(messageQueue));
        commandHandlers.add(new CommandCrud(messageQueue, commandService, userService));
        commandHandlers.add(new QuoteCrud(messageQueue, quoteService));
        commandHandlers.add(new RandomQuote(messageQueue, quoteService));
        commandHandlers.add(new TimerCrud(messageQueue, timerService, timerManager, userService));
        commandHandlers.add(new Betting(messageQueue, bettingService, userService));
        commandHandlers.add(new Points(messageQueue, userService));
        commandHandlers.add(new Giveaway(messageQueue, giveawayService));
        commandHandlers.add(new Auction(messageQueue, auctionService, userService));

        return commandHandlers;
    }

    @Override
    public void onShutdown(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            String schema = ConfigFactory.load().getConfig("seniorcommander").getString("database.schema");
            int result = session.createNativeQuery("DELETE FROM " + schema + ".online_channel_user").executeUpdate();
            logger.debug("Deleted " + result + " records from online_channel_user");
            session.getTransaction().commit();
            CoreExtension.TWITCH_MESSAGE_RATE_LIMITER.shutdown();
            CoreExtension.TWITCH_JOIN_RATE_LIMITER.shutdown();
        } catch (Exception ex) {
            logger.debug("Rolling back the deletion from online_channel_user");
            session.getTransaction().rollback();
            throw ex;
        } finally {
            session.close();
        }
    }
}
