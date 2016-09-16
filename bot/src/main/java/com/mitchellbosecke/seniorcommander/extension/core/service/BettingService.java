package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.BettingGameModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import org.hibernate.SessionFactory;

import java.util.Set;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class BettingService extends BaseService {

    public BettingService(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public BettingGameModel openBet(CommunityModel communityModel, Set<String> options){
        BettingGameModel game = new BettingGameModel();
        game.setCommunityModel(communityModel);
        game.setOptions(options);
        persist(game);
        return game;
    }

}
