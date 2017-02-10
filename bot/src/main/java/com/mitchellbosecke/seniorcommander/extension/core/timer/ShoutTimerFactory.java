package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class ShoutTimerFactory extends AbstractTimerFactory<ShoutTimer> {

    public ShoutTimerFactory(SeniorCommander seniorCommander) {
        super(seniorCommander);
    }

    @Override
    protected ShoutTimer constructTimerFromModel(SeniorCommander seniorCommander, TimerModel timerModel) {
        return new ShoutTimer(timerModel.getId(), timerModel.getInterval(), timerModel.getChannelModel()
                .getId(), seniorCommander, timerModel.getMessage());
    }

    @Override
    protected Class<ShoutTimer> getTimerClass() {
        return ShoutTimer.class;
    }
}
