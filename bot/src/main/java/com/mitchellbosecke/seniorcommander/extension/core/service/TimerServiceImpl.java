package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.Timer;
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
    public Timer addTimer(Community community, String message, long interval, long chatLines) {
        Long communitySequence;

        communitySequence = sessionFactory.getCurrentSession()
                .createQuery("SELECT max(t.communitySequence) FROM Timer t WHERE t.community = :community", Long.class)
                .setParameter("community", community).getSingleResult();

        communitySequence = communitySequence == null ? 0 : communitySequence;
        communitySequence++;

        Timer timer = new Timer();
        timer.setCommunity(community);
        timer.setCommunitySequence(communitySequence);
        timer.setMessage(message);
        timer.setInterval(interval);
        timer.setChatLines(chatLines);
        timer.setEnabled(true);
        persist(timer);
        return timer;
    }

    @Override
    public Timer findTimer(Community community, long communitySequence) {
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT t " +
                            "FROM Timer t " +
                            "WHERE t.community = :community " +
                            "AND t.communitySequence = :communitySequence", Timer.class)
                    .setParameter("community", community).setParameter("communitySequence", communitySequence)
                    .setMaxResults(1).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
