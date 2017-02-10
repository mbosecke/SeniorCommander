package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class PointTimerFactory extends AbstractTimerFactory<PointTimer> {

    private final UserService userService;

    public PointTimerFactory(SeniorCommander seniorCommander, UserService userService) {
        super(seniorCommander);
        this.userService = userService;
    }

    @Override
    protected PointTimer constructTimerFromModel(SeniorCommander seniorCommander, TimerModel timerModel) {
        return new PointTimer(timerModel.getId(), timerModel.getInterval(), timerModel.getChannelModel()
                .getId(), seniorCommander, userService);
    }

    @Override
    protected Class<PointTimer> getTimerClass() {
        return PointTimer.class;
    }
}
