package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.*;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;
import org.hibernate.Session;

import javax.persistence.NoResultException;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class CommandService extends BaseService {

    public void addCommand(CommunityModel communityModel, String trigger, String message, long cooldown,
                           AccessLevel accessLevel) {
        CommandModel commandModel = new CommandModel();
        commandModel.setCommunityModel(communityModel);
        commandModel.setTrigger(trigger);
        commandModel.setMessage(message);
        commandModel.setCooldown(cooldown);
        commandModel.setEnabled(true);
        commandModel.setAccessLevel(accessLevel);
        persist(commandModel);
    }

    public void deleteCommand(CommunityModel communityModel, String trigger) {

        CommandModel commandModel = findCommand(communityModel, trigger);
        Session session = TransactionManager.getCurrentSession();
        // commandModel logs
        session.createQuery("DELETE FROM CommandLogModel cl WHERE cl.commandModel = :command")
                .setParameter("command", commandModel).executeUpdate();

        session.delete(commandModel);
    }

    public CommandLogModel findMostRecentCommandLog(CommandModel commandModel, CommunityUserModel communityUserModel) {

        CommandLogModel log = null;
        try {
            //@formatter:off
            log = TransactionManager.getCurrentSession()
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

    public CommandModel findCommand(CommunityModel communityModel, String trigger) {
        try {
            //@formatter:off
            return TransactionManager.getCurrentSession()
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
