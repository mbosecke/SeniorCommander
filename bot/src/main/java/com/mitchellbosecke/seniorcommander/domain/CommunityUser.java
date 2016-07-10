package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;

/**
 * Created by mitch_000 on 2016-07-10.
 */
@Entity
@Table(name = "community_user")
public class CommunityUser {

    @Id
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @Column
    private String name;

    @Column
    private int points;

    @Column(name = "access_level")
    private String accessLevel;

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
