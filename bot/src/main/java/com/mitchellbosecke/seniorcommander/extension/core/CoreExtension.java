package com.mitchellbosecke.seniorcommander.extension.core;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.DiscordChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.SocketChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.command.*;
import com.mitchellbosecke.seniorcommander.extension.core.event.*;
import com.mitchellbosecke.seniorcommander.extension.core.service.*;
import com.mitchellbosecke.seniorcommander.extension.core.timer.*;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.utils.ConfigUtils;
import com.mitchellbosecke.seniorcommander.utils.NetworkUtils;
import com.mitchellbosecke.seniorcommander.utils.RateLimiter;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;
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
    public List<ChannelFactory> buildChannelFactories() {
        List<ChannelFactory> factories = new ArrayList<>();
        factories.add(new TwitchChannelFactory());
        factories.add(new SocketChannelFactory());
        factories.add(new DiscordChannelFactory());
        return factories;
    }

    @Override
    public List<Timer> buildTimers(SeniorCommander seniorCommander) {

        UserService userService = new UserService();
        List<Timer> timers = new ArrayList<>();
        new ShoutTimerFactory(seniorCommander).build().forEach(timers::add);
        new PointTimerFactory(seniorCommander, userService).build().forEach(timers::add);
        new TwitchOnlineCheckerFactory(seniorCommander).build().forEach(timers::add);
        new FollowerAuditFactory(seniorCommander, userService).build().forEach(timers::add);
        new ModAuditFactory(seniorCommander, userService).build().forEach(timers::add);
        return timers;
    }

    @Override
    public List<EventHandler> buildEventHandlers(SeniorCommander seniorCommander) {

        List<EventHandler> eventHandlers = new ArrayList<>();

        // service tiers
        UserService userService = new UserService();
        CommandService commandService = new CommandService();
        ChannelService channelService = new ChannelService();
        GiveawayService giveawayService = new GiveawayService();

        // handlers
        eventHandlers.add(new ChannelManagementHandler(seniorCommander));
        eventHandlers.add(new LoggingHandler(userService));
        eventHandlers.add(new OutputHandler(seniorCommander, channelService, userService));
        eventHandlers.add(new AiHandler(seniorCommander.getMessageQueue()));
        eventHandlers.add(new UserChatHandler(userService));
        eventHandlers.add(new JoinPartHandler(userService));
        eventHandlers.add(new NamesHandler(userService));
        eventHandlers.add(new CommandBroker(seniorCommander.getMessageQueue(), seniorCommander
                .getCommandHandlers(), userService, commandService));
        eventHandlers.add(new ModListHandler(userService));
        eventHandlers.add(new GiveawayKeywordHandler(giveawayService, userService));

        return eventHandlers;
    }

    @Override
    public List<CommandHandler> buildCommandHandlers(SeniorCommander seniorCommander) {

        // service tiers
        CommandService commandService = new CommandService();
        QuoteService quoteService = new QuoteService();
        TimerService timerService = new TimerService();
        UserService userService = new UserService();
        BettingService bettingService = new BettingService();
        GiveawayService giveawayService = new GiveawayService();
        AuctionService auctionService = new AuctionService();

        // handlers
        List<CommandHandler> commandHandlers = new ArrayList<>();
        commandHandlers.add(new Roll(seniorCommander));
        commandHandlers.add(new Advice(seniorCommander));
        commandHandlers.add(new Roulette(seniorCommander));
        commandHandlers.add(new CommandCrud(seniorCommander, commandService, userService));
        commandHandlers.add(new QuoteCrud(seniorCommander, quoteService));
        commandHandlers.add(new RandomQuote(seniorCommander, quoteService));
        commandHandlers.add(new TimerCrud(seniorCommander, timerService, userService));
        commandHandlers.add(new Betting(seniorCommander, bettingService, userService));
        commandHandlers.add(new Points(seniorCommander, userService));
        commandHandlers.add(new Giveaway(seniorCommander, giveawayService));
        commandHandlers.add(new Auction(seniorCommander, auctionService, userService));

        return commandHandlers;
    }

    @Override
    public void onShutdown() {
        logger.debug("shutting down core extension");
        TransactionManager.runInTransaction(session -> {

            String schema = ConfigUtils.getString("database.schema");
            //@formatter:off
            int result = session.createNativeQuery("" +
                    "DELETE FROM " + schema + ".online_channel_user ocu " +
                    "WHERE channel_id IN (" +
                    "   SELECT c.id " +
                    "   FROM " + schema + ".channel c " +
                    "   JOIN " + schema + ".community co ON co.id = c.community_id" +
                    "   WHERE co.server = '" + NetworkUtils.getLocalHostname() + "' " +
                    ")").executeUpdate();
            //@formatter:on
            logger.debug("Deleted " + result + " records from online_channel_user");

            CoreExtension.TWITCH_MESSAGE_RATE_LIMITER.shutdown();
            CoreExtension.TWITCH_JOIN_RATE_LIMITER.shutdown();

        });
    }
}
