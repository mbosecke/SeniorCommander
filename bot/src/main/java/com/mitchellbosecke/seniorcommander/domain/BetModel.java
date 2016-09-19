package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;

/**
 * Created by mitch_000 on 2016-07-12.
 */
@Entity
@Table(name = "bet")
public class BetModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_user_id")
    private CommunityUserModel communityUserModel;

    @ManyToOne
    @JoinColumn(name = "betting_option_id")
    private BettingOptionModel bettingOptionModel;

    @Column
    private int amount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CommunityUserModel getCommunityUserModel() {
        return communityUserModel;
    }

    public void setCommunityUserModel(CommunityUserModel communityUserModel) {
        this.communityUserModel = communityUserModel;
    }

    public BettingOptionModel getBettingOptionModel() {
        return bettingOptionModel;
    }

    public void setBettingOptionModel(BettingOptionModel bettingOptionModel) {
        this.bettingOptionModel = bettingOptionModel;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
