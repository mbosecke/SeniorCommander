package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Auction
 */
@Entity
@Table(name = "auction")
public class AuctionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityModel communityModel;

    @Column
    private String prize;

    @Column
    private ZonedDateTime closed;

    @ManyToOne
    @JoinColumn(name = "winning_community_user_id")
    private CommunityUserModel winningCommunityUserModel;

    @Column(name = "winning_bid")
    private Integer winningBid;

    @Column
    private ZonedDateTime cancelled;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CommunityModel getCommunityModel() {
        return communityModel;
    }

    public void setCommunityModel(CommunityModel communityModel) {
        this.communityModel = communityModel;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public ZonedDateTime getClosed() {
        return closed;
    }

    public void setClosed(ZonedDateTime closed) {
        this.closed = closed;
    }

    public CommunityUserModel getWinningCommunityUserModel() {
        return winningCommunityUserModel;
    }

    public void setWinningCommunityUserModel(CommunityUserModel winningCommunityUserModel) {
        this.winningCommunityUserModel = winningCommunityUserModel;
    }

    public Integer getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(Integer winningBid) {
        this.winningBid = winningBid;
    }

    public ZonedDateTime getCancelled() {
        return cancelled;
    }

    public void setCancelled(ZonedDateTime cancelled) {
        this.cancelled = cancelled;
    }
}



