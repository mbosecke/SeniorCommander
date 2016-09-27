package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.timer.ShoutTimer;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class TimerService extends BaseService {


    public TimerService(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public TimerModel addTimer(Channel channel, String message, long interval, long chatLines) {
        Long communitySequence;
        ChannelModel channelModel = find(ChannelModel.class, channel.getId());

        communitySequence = sessionFactory.getCurrentSession()
                .createQuery("SELECT max(t.communitySequence) FROM TimerModel t WHERE t.channelModel.communityModel = :community",
                        Long
                                .class)
                .setParameter("community", channelModel.getCommunityModel()).getSingleResult();

        communitySequence = communitySequence == null ? 0 : communitySequence;
        communitySequence++;

        TimerModel timerModel = new TimerModel();
        timerModel.setChannelModel(channelModel);
        timerModel.setCommunitySequence(communitySequence);
        timerModel.setMessage(message);
        timerModel.setInterval(interval);
        timerModel.setChatLines(chatLines);
        timerModel.setEnabled(true);
        timerModel.setImplementation(ShoutTimer.class.getName());
        persist(timerModel);
        return timerModel;
    }

    public TimerModel findTimer(CommunityModel communityModel, long communitySequence) {
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT t " +
                            "FROM TimerModel t " +
                            "WHERE t.channelModel.communityModel = :community " +
                            "AND t.communitySequence = :communitySequence", TimerModel.class)
                    .setParameter("community", communityModel).setParameter("communitySequence", communitySequence)
                    .setMaxResults(1).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }
}
