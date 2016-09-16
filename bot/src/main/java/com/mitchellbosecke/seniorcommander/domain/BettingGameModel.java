package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
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

    @ElementCollection
    @CollectionTable(name = "betting_option", joinColumns = @JoinColumn(name = "betting_game_id"))
    @Column(name = "value")
    private Set<String> options;

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

    public Set<String> getOptions() {
        return options;
    }

    public void setOptions(Set<String> options) {
        this.options = options;
    }
}
