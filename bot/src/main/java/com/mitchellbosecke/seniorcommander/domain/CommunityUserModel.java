package com.mitchellbosecke.seniorcommander.domain;

import com.mitchellbosecke.seniorcommander.AccessLevel;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-10.
 */
@Entity
@Table(name = "community_user")
public class CommunityUserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityModel communityModel;

    @Column
    private String name;

    @Column
    private int points;

    @Column(name = "access_level")
    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    @Column(name = "first_seen")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date firstSeen;

    @Column(name = "last_chatted")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastChatted;

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public CommunityModel getCommunityModel() {
        return communityModel;
    }

    public void setCommunityModel(CommunityModel communityModel) {
        this.communityModel = communityModel;
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

    public Date getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Date firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Date getLastChatted() {
        return lastChatted;
    }

    public void setLastChatted(Date lastChatted) {
        this.lastChatted = lastChatted;
    }
}
