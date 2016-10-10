package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-31.
 */
@Entity
@Table(name = "timer")
public class TimerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelModel channelModel;

    @Column(name = "community_sequence")
    private long communitySequence;

    @Column
    private String message;

    @Column
    private String implementation;

    @Column
    private long interval;

    @Column(name = "chat_lines")
    private Long chatLines;

    @Column
    private boolean enabled;

    @Column(name = "last_executed")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastExecuted;

    @Column
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ChannelModel getChannelModel() {
        return channelModel;
    }

    public void setChannelModel(ChannelModel channelModel) {
        this.channelModel = channelModel;
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

    public Long getChatLines() {
        return chatLines;
    }

    public void setChatLines(Long chatLines) {
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

    public Date getLastExecuted() {
        return lastExecuted;
    }

    public void setLastExecuted(Date lastExecuted) {
        this.lastExecuted = lastExecuted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
