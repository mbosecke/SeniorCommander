package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;

import java.util.List;
import java.util.Map;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class PointTimerFactory extends AbstractTimerFactory<PointTimer> {

    private final UserService userService;

    public PointTimerFactory(List<Channel> channels, UserService userService) {
        super(channels);
        this.userService = userService;
    }

    @Override
    protected PointTimer constructTimerFromModel(TimerModel timerModel, Map<Long, Channel> channels) {
        return new PointTimer(timerModel.getId(), timerModel.getInterval(), channels
                .get(timerModel.getChannelModel().getId()), userService);
    }

    @Override
    protected Class<PointTimer> getTimerClass() {
        return PointTimer.class;
    }
}
