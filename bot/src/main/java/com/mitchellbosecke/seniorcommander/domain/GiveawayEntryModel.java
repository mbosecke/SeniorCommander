package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;

/**
 * Giveaway entry
 */
@Entity
@Table(name = "giveaway_entry")
public class GiveawayEntryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "giveaway_id")
    private GiveawayModel giveawayModel;

    @ManyToOne
    @JoinColumn(name = "community_user_id")
    private CommunityUserModel communityUserModel;

    @Column
    private Boolean winner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GiveawayModel getGiveawayModel() {
        return giveawayModel;
    }

    public void setGiveawayModel(GiveawayModel giveawayModel) {
        this.giveawayModel = giveawayModel;
    }

    public CommunityUserModel getCommunityUserModel() {
        return communityUserModel;
    }

    public void setCommunityUserModel(CommunityUserModel communityUserModel) {
        this.communityUserModel = communityUserModel;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }
}
