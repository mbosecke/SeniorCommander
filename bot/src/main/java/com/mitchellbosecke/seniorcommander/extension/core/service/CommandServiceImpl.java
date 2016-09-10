package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.domain.CommandModel;
import com.mitchellbosecke.seniorcommander.domain.CommandLogModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
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
    public void addCommand(CommunityModel communityModel, String trigger, String message, long cooldown, AccessLevel
            accessLevel) {
        CommandModel commandModel = new CommandModel();
        commandModel.setCommunityModel(communityModel);
        commandModel.setTrigger(trigger);
        commandModel.setMessage(message);
        commandModel.setCooldown(cooldown);
        commandModel.setEnabled(true);
        commandModel.setAccessLevel(accessLevel);
        persist(commandModel);
    }

    @Override
    public void deleteCommand(CommunityModel communityModel, String trigger) {

        CommandModel commandModel = findCommand(communityModel, trigger);
        Session session = sessionFactory.getCurrentSession();
        // commandModel logs
        session.createQuery("DELETE FROM CommandLogModel cl WHERE cl.command = :command").setParameter("command",
                commandModel)
                .executeUpdate();

        session.delete(commandModel);
    }

    @Override
    public CommandLogModel findMostRecentCommandLog(CommandModel commandModel, CommunityUserModel communityUserModel) {

        CommandLogModel log = null;
        try {
            //@formatter:off
            log = sessionFactory.getCurrentSession()
                    .createQuery("SELECT cl " +
                            "FROM CommandLogModel cl " +
                            "WHERE cl.commandModel = :command " +
                            "AND cl.communityUserModel = :user " +
                            "ORDER BY cl.logDate desc ", CommandLogModel.class)
                    .setParameter("command", commandModel).setParameter("user", communityUserModel).setMaxResults(1)
                    .getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
        }
        return log;
    }

    @Override
    public CommandModel findCommand(CommunityModel communityModel, String trigger) {
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT c " +
                            "FROM CommandModel c " +
                            "WHERE c.communityModel = :community " +
                            "AND :trigger LIKE (c.trigger || '%') " +
                            "ORDER BY trigger DESC", CommandModel.class)
                    .setParameter("community", communityModel).setParameter("trigger", trigger.toLowerCase())
                    .setMaxResults(1).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
