package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class TwitchOnlineCheckerFactory extends AbstractTimerFactory<TwitchOnlineChecker> {

    public TwitchOnlineCheckerFactory(SeniorCommander seniorCommander) {
        super(seniorCommander);
    }

    @Override
    protected TwitchOnlineChecker constructTimerFromModel(SeniorCommander seniorCommander, TimerModel timerModel) {
        return new TwitchOnlineChecker(timerModel.getId(), timerModel.getInterval(), timerModel.getChannelModel()
                .getId(), seniorCommander);
    }

    @Override
    protected Class<TwitchOnlineChecker> getTimerClass() {
        return TwitchOnlineChecker.class;
    }
}
