package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.*;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class BettingService extends BaseService {

    public BettingService(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public BettingGameModel openBet(CommunityModel communityModel, Set<String> options) {
        BettingGameModel game = new BettingGameModel();
        game.setCommunityModel(communityModel);
        game.setOptions(new HashSet<>());
        persist(game);
        for (String option : options) {
            BettingOptionModel bettingOption = new BettingOptionModel();
            bettingOption.setBettingGameModel(game);
            bettingOption.setValue(option);
            persist(bettingOption);
            game.getOptions().add(bettingOption);
        }

        return game;
    }

    public void cancelBet(CommunityModel communityModel) {
        BettingGameModel game = communityModel.getBettingGameModel();
        if (game != null) {
            delete(game);
        }
    }

    public void placeBet(CommunityUserModel user, BettingOptionModel option, int amount) {
        if (user.getPoints() >= amount) {
            user.setPoints(user.getPoints() - amount);

            BetModel bet = new BetModel();
            bet.setBettingOptionModel(option);
            bet.setCommunityUserModel(user);
            bet.setAmount(amount);
            persist(bet);
        } else {
            throw new RuntimeException("User does not have enough points");
        }
    }

    public BetModel getBet(CommunityUserModel user, BettingGameModel game) {
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT bet " +
                            "FROM BetModel bet " +
                            "WHERE bet.communityUserModel = :user", BetModel.class)
                    .setParameter("user", user).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
