package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.ChannelService;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class OutputHandler implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<Channel> channels;

    private final ChannelService channelService;

    private final String CONFIG_MUTE = "mute";

    public OutputHandler(List<Channel> channels, ChannelService channelService) {
        this.channels = channels;
        this.channelService = channelService;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.OUTPUT.equals(message.getType())) {

            logger.debug("Output handler: " + message.getContent());
            List<Channel> outputChannels = new ArrayList<>();
            if (message.getChannel() != null) {
                outputChannels.add(message.getChannel());
            } else {
                outputChannels.addAll(channels);
            }

            for (Channel channel : outputChannels) {
                emit(channel, message.getRecipient(), message.getContent(), message.isWhisper());
            }
        }
    }

    private void emit(Channel channel, String recipient, String content, boolean whisper) {
        ChannelModel channelModel = channelService.find(ChannelModel.class, channel.getId());
        if (Boolean.valueOf(channelModel.getSetting(CONFIG_MUTE))) {
            return;
        }

        if (recipient != null) {
            if (whisper) {
                channel.sendWhisper(recipient, content);
            } else {
                channel.sendMessage(recipient, content);
            }
        } else {
            channel.sendMessage(content);
        }
    }
}
