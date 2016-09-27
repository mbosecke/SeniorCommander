package com.mitchellbosecke.seniorcommander.timer;

import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.utils.ExecutorUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-31.
 */
public class TimerManager {

    private static final Logger logger = LoggerFactory.getLogger(TimerManager.class);

    private final Map<Long, Timer> timers = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> ongoingTasks = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService;
    private final SessionFactory sessionFactory;

    public TimerManager(ScheduledExecutorService executorService, SessionFactory sessionFactory) {
        this.executorService = executorService;
        this.sessionFactory = sessionFactory;
    }

    public void addTimer(Timer timer) {
        startTimer(timer);
        timers.put(timer.getId(), timer);
    }

    public void disableTimer(long id) {
        if (ongoingTasks.containsKey(id)) {
            ongoingTasks.get(id).cancel(false);
        }
    }

    public void enableTimer(long id) {
        startTimer(timers.get(id));
    }

    private void startTimer(Timer timer) {

        // random initial delay to spread out timers
        long initialDelay = new Random().nextInt(Math.toIntExact(timer.getInterval()));

        ScheduledFuture<?> future = executorService.scheduleAtFixedRate(() -> {
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            try {
                TimerModel model = sessionFactory.getCurrentSession().find(TimerModel.class, timer.getId());
                if (satisfiesChatLineRequirement(model)) {
                    timer.perform();
                    model.setLastExecuted(new Date());
                }
                session.getTransaction().commit();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }, initialDelay, timer.getInterval(), TimeUnit.SECONDS);
        ongoingTasks.put(timer.getId(), future);
    }

    private boolean satisfiesChatLineRequirement(TimerModel model) {
        boolean satisfiesChatLineRequirement = false;

        if (model.getChatLines() != null && model.getChatLines() > 0) {
            Date dateLastExecuted = model.getLastExecuted();

            if (dateLastExecuted == null) {
                dateLastExecuted = new Date(0);
            }
            Long chatLines = (Long) sessionFactory.getCurrentSession()
                    .createQuery("SELECT count(*) FROM ChatLogModel clm WHERE clm.date >= :date AND clm.channel.id = :channelId")
                    .setParameter("date", dateLastExecuted).setParameter("channelId", model.getChannelModel().getId())
                    .uniqueResult();
            satisfiesChatLineRequirement = chatLines >= model.getChatLines();

            if(satisfiesChatLineRequirement){
                logger.debug("Satisfies chat lines requirement");
            }else{
                long diff = model.getChatLines() - chatLines;
                logger.debug("Requires " + diff + " more chat lines");
            }

        } else {
            satisfiesChatLineRequirement = true;
        }

        return satisfiesChatLineRequirement;
    }

    public void shutdown() {
        ExecutorUtils.shutdown(executorService, 5, TimeUnit.SECONDS);
    }

}
