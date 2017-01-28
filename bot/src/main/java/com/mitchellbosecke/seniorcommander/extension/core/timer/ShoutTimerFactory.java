package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;

import java.util.List;
import java.util.Map;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class ShoutTimerFactory extends AbstractTimerFactory<ShoutTimer> {

    private final MessageQueue messageQueue;

    public ShoutTimerFactory(List<Channel> channels, MessageQueue messageQueue) {
        super(channels);
        this.messageQueue = messageQueue;
    }

    @Override
    protected ShoutTimer constructTimerFromModel(TimerModel timerModel, Map<Long, Channel> channels) {
        return new ShoutTimer(timerModel.getId(), timerModel.getInterval(), channels
                .get(timerModel.getChannelModel().getId()), messageQueue, timerModel.getMessage());
    }

    @Override
    protected Class<ShoutTimer> getTimerClass() {
        return ShoutTimer.class;
    }
}
