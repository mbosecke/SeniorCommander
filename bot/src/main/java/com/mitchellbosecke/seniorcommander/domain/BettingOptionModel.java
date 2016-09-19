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
@Table(name = "betting_option")
public class BettingOptionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "betting_game_id")
    private BettingGameModel bettingGameModel;

    @OneToMany(mappedBy = "bettingOptionModel")
    @Cascade(CascadeType.ALL)
    private Set<BetModel> bets;

    @Column
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BettingGameModel getBettingGameModel() {
        return bettingGameModel;
    }

    public void setBettingGameModel(BettingGameModel bettingGameModel) {
        this.bettingGameModel = bettingGameModel;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Set<BetModel> getBets() {
        return bets;
    }

    public void setBets(Set<BetModel> bets) {
        this.bets = bets;
    }
}
