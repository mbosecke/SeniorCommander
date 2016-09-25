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

    @Column(name = "first_followed")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date firstFollowed;

    @Column(name = "last_followed")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastFollowed;

    @Column(name = "last_online")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastOnline;

    @Column(name = "time_online")
    private long timeOnline;

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

    public Date getFirstFollowed() {
        return firstFollowed;
    }

    public void setFirstFollowed(Date firstFollowed) {
        this.firstFollowed = firstFollowed;
    }

    public Date getLastFollowed() {
        return lastFollowed;
    }

    public void setLastFollowed(Date lastFollowed) {
        this.lastFollowed = lastFollowed;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public long getTimeOnline() {
        return timeOnline;
    }

    public void setTimeOnline(long timeOnline) {
        this.timeOnline = timeOnline;
    }
}
