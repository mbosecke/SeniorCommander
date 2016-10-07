package com.mitchellbosecke.seniorcommander.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

/**
 * Created by mitch_000 on 2016-07-12.
 */
@Entity
@Table(name = "betting_game")
public class BettingGameModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityModel communityModel;

    @Column
    private boolean closed;

    @OneToMany(mappedBy = "bettingGameModel")
    @Cascade(CascadeType.ALL)
    private Set<BettingOptionModel> options;

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

    public Set<BettingOptionModel> getOptions() {
        return options;
    }

    public void setOptions(Set<BettingOptionModel> options) {
        this.options = options;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
