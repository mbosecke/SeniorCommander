package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2017-01-28.
 */
public class ChannelManagementHandler implements EventHandler {

    private final SeniorCommander seniorCommander;

    private static final Logger logger = LoggerFactory.getLogger(ChannelManagementHandler.class);

    public ChannelManagementHandler(SeniorCommander seniorCommander) {
        this.seniorCommander = seniorCommander;
    }

    @Override
    public void handle(Message message) {

        switch (message.getType()) {
            case CHANNEL_START:
                logger.debug("Starting channel [{}]", message.getChannel());
                seniorCommander.getChannelManager().startChannel(message.getChannel(), true);
                break;
            case CHANNEL_STOP:
                logger.debug("Stopping channel [{}]", message.getChannel());
                seniorCommander.getChannelManager().stopChannel(message.getChannel());
                break;
            case CHANNEL_RESTART:
                logger.debug("Restarting channel [{}]", message.getChannel());
                seniorCommander.getChannelManager().stopChannel(message.getChannel());
                seniorCommander.getChannelManager().startChannel(message.getChannel(), true);
                break;
            default:
                // unhandled
                break;
        }
    }
}
