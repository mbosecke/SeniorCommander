package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-10.
 */
@Entity
@Table(name = "community_user")
public class CommunityUser {

    public enum AccessLevel {
        USER, MODERATOR, ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "first_seen")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date firstSeen;

    @Column(name = "last_chatted")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastChatted;

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
