package com.mitchellbosecke.seniorcommander.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Giveaway
 */
@Entity
@Table(name = "giveaway")
public class GiveawayModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityModel communityModel;

    @Column
    private String keyword;

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date closed;

    @OneToMany(mappedBy = "giveawayModel")
    @Cascade(CascadeType.ALL)
    private Set<GiveawayEntryModel> entries;

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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getClosed() {
        return closed;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    public Set<GiveawayEntryModel> getEntries() {
        return entries;
    }

    public void setEntries(Set<GiveawayEntryModel> entries) {
        this.entries = entries;
    }
}



