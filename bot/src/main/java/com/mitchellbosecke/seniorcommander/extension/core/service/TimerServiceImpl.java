package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.task.Shout;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class TimerServiceImpl extends BaseServiceImpl implements TimerService {

    public TimerServiceImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public TimerModel addTimer(CommunityModel communityModel, String message, long interval, long chatLines) {
        Long communitySequence;

        communitySequence = sessionFactory.getCurrentSession()
                .createQuery("SELECT max(t.communitySequence) FROM TimerModel t WHERE t.communityModel = :community",
                        Long
                        .class)
                .setParameter("community", communityModel).getSingleResult();

        communitySequence = communitySequence == null ? 0 : communitySequence;
        communitySequence++;

        TimerModel timerModel = new TimerModel();
        timerModel.setCommunityModel(communityModel);
        timerModel.setCommunitySequence(communitySequence);
        timerModel.setMessage(message);
        timerModel.setInterval(interval);
        timerModel.setChatLines(chatLines);
        timerModel.setEnabled(true);
        timerModel.setImplementation(Shout.class.getName());
        persist(timerModel);
        return timerModel;
    }

    @Override
    public TimerModel findTimer(CommunityModel communityModel, long communitySequence) {
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT t " +
                            "FROM TimerModel t " +
                            "WHERE t.communityModel = :community " +
                            "AND t.communitySequence = :communitySequence", TimerModel.class)
                    .setParameter("community", communityModel).setParameter("communitySequence", communitySequence)
                    .setMaxResults(1).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
