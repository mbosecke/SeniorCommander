package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;

/**
 * Created by mitch_000 on 2016-07-31.
 */
@Entity
@Table(name = "timer")
public class Timer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @Column(name = "community_sequence")
    private long communitySequence;

    @Column
    private String message;

    @Column
    private String implementation;

    @Column
    private long interval;

    @Column(name = "chat_lines")
    private long chatLines;

    @Column
    private boolean enabled;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getChatLines() {
        return chatLines;
    }

    public void setChatLines(long chatLines) {
        this.chatLines = chatLines;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getCommunitySequence() {
        return communitySequence;
    }

    public void setCommunitySequence(long communitySequence) {
        this.communitySequence = communitySequence;
    }
}
