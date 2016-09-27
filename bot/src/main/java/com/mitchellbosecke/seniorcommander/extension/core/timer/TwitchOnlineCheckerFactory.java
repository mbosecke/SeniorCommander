package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class TwitchOnlineCheckerFactory extends AbstractTimerFactory<TwitchOnlineChecker> {

    public TwitchOnlineCheckerFactory(SessionFactory sessionFactory, List<Channel> channels) {
        super(sessionFactory, channels);
    }

    @Override
    protected TwitchOnlineChecker constructTimerFromModel(TimerModel timerModel, SessionFactory sessionFactory,
                                                          Map<Long, Channel> channels) {
        return new TwitchOnlineChecker(timerModel.getId(), timerModel.getInterval(), (TwitchChannel) channels
                .get(timerModel.getChannelModel().getId()));
    }

    @Override
    protected Class<TwitchOnlineChecker> getTimerClass() {
        return TwitchOnlineChecker.class;
    }
}
