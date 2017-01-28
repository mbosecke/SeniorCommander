package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.domain.GiveawayEntryModel;
import com.mitchellbosecke.seniorcommander.domain.GiveawayModel;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;

import javax.persistence.NoResultException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;

public class GiveawayService extends BaseService {

    public GiveawayModel openGiveaway(CommunityModel communityModel, String keyword) {
        GiveawayModel giveaway = new GiveawayModel();
        giveaway.setCommunityModel(communityModel);
        giveaway.setEntries(new HashSet<>());
        giveaway.setKeyword(keyword);
        persist(giveaway);
        return giveaway;
    }

    /**
     * Cancels a giveaway
     *
     * @param communityModel
     */
    public void cancelGiveaway(CommunityModel communityModel) {
        GiveawayModel giveaway = findActiveGiveaway(communityModel);
        if (giveaway != null) {
            giveaway.setClosed(ZonedDateTime.now(ZoneId.of("UTC")));
        }
    }

    /**
     * @return A winner
     */
    public Optional<String> drawWinner(GiveawayModel giveaway) {
        giveaway.setClosed(ZonedDateTime.now(ZoneId.of("UTC")));

        String winner = null;
        try {
            GiveawayEntryModel winningEntry = null;
            //@formatter:off
            winningEntry = TransactionManager.getCurrentSession()
                    .createQuery("SELECT e " +
                            "FROM GiveawayEntryModel e " +
                            "WHERE e.winner = false " +
                            "AND e.giveawayModel = :giveaway", GiveawayEntryModel.class)
                    .setParameter("giveaway", giveaway).setMaxResults(1).getSingleResult();
            //@formatter:on

            winningEntry.setWinner(true);
            winner = winningEntry.getCommunityUserModel().getName();
        } catch (NoResultException ex) {
        }
        return Optional.ofNullable(winner);
    }

    public void enterGiveaway(CommunityUserModel user) {
        GiveawayModel giveaway = findActiveGiveaway(user.getCommunityModel());

        if (giveaway != null) {

            GiveawayEntryModel existingEntry = findGiveawayEntry(giveaway, user);

            if (existingEntry == null) {
                GiveawayEntryModel entry = new GiveawayEntryModel();
                entry.setGiveawayModel(giveaway);
                entry.setCommunityUserModel(user);
                entry.setWinner(false);
                persist(entry);
            }
        }
    }

    private GiveawayEntryModel findGiveawayEntry(GiveawayModel giveaway, CommunityUserModel user) {
        try {
            //@formatter:off
            return TransactionManager.getCurrentSession()
                    .createQuery("SELECT e " +
                            "FROM GiveawayEntryModel e " +
                            "WHERE e.giveawayModel = :giveaway " +
                            "AND e.communityUserModel = :user", GiveawayEntryModel.class)
                    .setParameter("giveaway", giveaway).setParameter("user", user).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

    public GiveawayModel findActiveGiveaway(CommunityModel communityModel) {
        try {
            //@formatter:off
            return TransactionManager.getCurrentSession()
                    .createQuery("SELECT g " +
                            "FROM GiveawayModel g " +
                            "WHERE g.closed IS NULL " +
                            "AND g.communityModel = :community", GiveawayModel.class)
                    .setParameter("community", communityModel).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

    public GiveawayModel findMostRecentGiveaway(CommunityModel communityModel) {
        try {
            //@formatter:off
            return TransactionManager.getCurrentSession()
                    .createQuery("SELECT g " +
                            "FROM GiveawayModel g " +
                            "WHERE g.communityModel = :community " +
                            "ORDER BY g.closed ASC NULLS FIRST", GiveawayModel.class)
                    .setParameter("community", communityModel).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
