package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.utils.NetworkUtils;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mitch_000 on 2016-09-26.
 */
public abstract class AbstractTimerFactory<T extends Timer> {

    private final List<Channel> channels;

    public AbstractTimerFactory(List<Channel> channels) {
        this.channels = channels;
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

        Map<Long, Channel> channelMap = channels.stream().collect(Collectors.toMap(Channel::getId, c -> c));

        for (TimerModel timerModel : timerModels) {
            timers.add(constructTimerFromModel(timerModel, channelMap));
        }
        return timers;
    }

    protected abstract T constructTimerFromModel(TimerModel timerModel, Map<Long, Channel> channels);

    protected abstract Class<T> getTimerClass();
}
