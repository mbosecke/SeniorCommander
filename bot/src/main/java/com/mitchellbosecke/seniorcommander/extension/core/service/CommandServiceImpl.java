package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.*;
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
        persist(command);
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
    public Command findCommand(Channel channel, String trigger) {
        ChannelConfiguration channelConfig = find(ChannelConfiguration.class, channel.getId());
        Community community = channelConfig.getCommunity();
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT c " +
                            "FROM Command c " +
                            "WHERE c.community = :community " +
                            "AND c.trigger = :trigger ", Command.class)
                    .setParameter("community", community).setParameter("trigger", trigger.toLowerCase()).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
