package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Configuration;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class OutputHandler implements MessageHandler {

    public static final String CONFIG_MUTE = "output.mute";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Configuration configuration;

    private final List<Channel> channels;

    public OutputHandler(Configuration configuration, List<Channel> channels) {
        this.configuration = configuration;
        this.channels = channels;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.OUTPUT.equals(message.getType())) {

            if (!isMute()) {
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
    }

    private boolean isMute() {
        String isMute = configuration.getProperty(CONFIG_MUTE);
        return isMute == null || Boolean.valueOf(isMute);
    }

    private void emit(Channel channel, String recipient, String content, boolean whisper) {
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
