package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.AuctionModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;

import javax.persistence.NoResultException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class AuctionService extends BaseService {

    public AuctionModel openAuction(CommunityModel communityModel, String prize) {
        AuctionModel auction = new AuctionModel();
        auction.setCommunityModel(communityModel);
        auction.setPrize(prize);
        persist(auction);
        return auction;
    }

    public void placeBid(AuctionModel auction, CommunityUserModel user, int amount){
        returnWinningBid(auction);
        auction.setWinningBid(amount);
        auction.setWinningCommunityUserModel(user);
        user.setPoints(user.getPoints() - amount);
    }

    /**
     * Cancels an auction
     *
     */
    public void cancelAuction(AuctionModel auction) {
        auction.setCancelled(ZonedDateTime.now(ZoneId.of("UTC")));
        returnWinningBid(auction);
    }

    private void returnWinningBid(AuctionModel auction){
        if(auction.getWinningCommunityUserModel() != null){
            CommunityUserModel user = auction.getWinningCommunityUserModel();
            user.setPoints(user.getPoints() + auction.getWinningBid());
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
            return TransactionManager.getCurrentSession()
                    .createQuery("SELECT a " +
                            "FROM AuctionModel a " +
                            "WHERE a.closed IS NULL " +
                            "AND a.cancelled IS NULL " +
                            "AND a.communityModel = :community", AuctionModel.class)
                    .setParameter("community", communityModel).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
            return null;
        }
    }

}
