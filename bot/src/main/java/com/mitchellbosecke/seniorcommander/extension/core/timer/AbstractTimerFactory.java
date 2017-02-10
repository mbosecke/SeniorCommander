package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.utils.NetworkUtils;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-09-26.
 */
public abstract class AbstractTimerFactory<T extends Timer> {

    private final SeniorCommander seniorCommander;

    public AbstractTimerFactory(SeniorCommander seniorCommander) {
        this.seniorCommander = seniorCommander;
    }

    public List<T> build() {
        List<T> timers = new ArrayList<>();

        //@formatter:off
        List<TimerModel> timerModels = TransactionManager.getCurrentSession()
                .createQuery("" +
                        "SELECT tm " +
                        "FROM TimerModel tm " +
                        "WHERE tm.implementation = :implementation " +
                        "AND tm.channelModel.communityModel.server = :server " +
                        "AND tm.enabled = true", TimerModel.class)
                .setParameter("implementation", getTimerClass().getName())
                .setParameter("server",  NetworkUtils.getLocalHostname())
                .getResultList();
        //@formatter:on

        for (TimerModel timerModel : timerModels) {
            timers.add(constructTimerFromModel(seniorCommander, timerModel));
        }
        return timers;
    }

    protected abstract T constructTimerFromModel(SeniorCommander seniorCommander, TimerModel timerModel);

    protected abstract Class<T> getTimerClass();
}
