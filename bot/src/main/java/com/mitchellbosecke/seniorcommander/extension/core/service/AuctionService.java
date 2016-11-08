package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.AuctionModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class AuctionService extends BaseService {

    public AuctionService(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public AuctionModel openAuction(CommunityModel communityModel, String prize) {
        AuctionModel auction = new AuctionModel();
        auction.setCommunityModel(communityModel);
        auction.setPrize(prize);
        persist(auction);
        return auction;
    }

    /**
     * Cancels an auction
     *
     * @param communityModel
     */
    public void cancelAuction(CommunityModel communityModel) {
        AuctionModel auction = findActiveAuction(communityModel);
        if (auction != null) {
            auction.setClosed(ZonedDateTime.now(ZoneId.of("UTC")));
        }
    }

    /**
     * Close an auction
     *
     * @return A winner
     */
    public Optional<CommunityUserModel> close(AuctionModel auction) {
        auction.setClosed(ZonedDateTime.now(ZoneId.of("UTC")));
        return Optional.ofNullable(auction.getWinningCommunityUserModel());
    }

    public AuctionModel findActiveAuction(CommunityModel communityModel) {
        try {
            //@formatter:off
            return sessionFactory.getCurrentSession()
                    .createQuery("SELECT a " +
                            "FROM AuctionModel a " +
                            "WHERE a.closed IS NULL " +
                            "AND a.communityModel = :community", AuctionModel.class)
                    .setParameter("community", communityModel).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
