package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.domain.Command;
import com.mitchellbosecke.seniorcommander.domain.CommandLog;
import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class CommandServiceImpl extends BaseServiceImpl implements CommandService {

    public CommandServiceImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void addCommand(Community community, String trigger, String message, long cooldown) {
        Command command = new Command();
        command.setCommunity(community);
        command.setTrigger(trigger);
        command.setMessage(message);
        command.setCooldown(cooldown);
        command.setEnabled(true);
        command.setAccessLevel(AccessLevel.USER);
        persist(command);
    }

    @Override
    public void deleteCommand(Community community, String trigger) {

        Command command = findCommand(community, trigger);
        Session session = sessionFactory.getCurrentSession();
        // command logs
        session.createQuery("DELETE FROM CommandLog cl WHERE cl.command = :command").setParameter("command", command)
                .executeUpdate();

        session.delete(command);
    }

    @Override
    public CommandLog findMostRecentCommandLog(Command command, CommunityUser communityUser) {

        CommandLog log = null;
        try {
            //@formatter:off
            log = sessionFactory.getCurrentSession()
                    .createQuery("SELECT cl " +
                            "FROM CommandLog cl " +
                            "WHERE cl.command = :command " +
                            "AND cl.communityUser = :user " +
                            "ORDER BY cl.logDate desc ", CommandLog.class)
                    .setParameter("command", command).setParameter("user", communityUser).setMaxResults(1)
                    .getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
        }
        return log;
    }

    @Override
    public Command findCommand(Community community, String trigger) {
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT c " +
                            "FROM Command c " +
                            "WHERE c.community = :community " +
                            "AND :trigger LIKE (c.trigger || '%') " +
                            "ORDER BY trigger DESC", Command.class)
                    .setParameter("community", community).setParameter("trigger", trigger.toLowerCase())
                    .setMaxResults(1).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
