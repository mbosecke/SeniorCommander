package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.utils.NetworkUtils;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mitch_000 on 2016-09-26.
 */
public abstract class AbstractTimerFactory<T extends Timer> {

    private final SessionFactory sessionFactory;
    private final List<Channel> channels;

    public AbstractTimerFactory(SessionFactory sessionFactory, List<Channel> channels) {
        this.sessionFactory = sessionFactory;
        this.channels = channels;
    }

    public List<T> build() {
        List<T> timers = new ArrayList<>();

        //@formatter:off
        List<TimerModel> timerModels = sessionFactory.getCurrentSession()
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
            timers.add(constructTimerFromModel(timerModel, sessionFactory, channelMap));
        }
        return timers;
    }

    protected abstract T constructTimerFromModel(TimerModel timerModel, SessionFactory sessionFactory,
                                                 Map<Long, Channel> channels);

    protected abstract Class<T> getTimerClass();
}
