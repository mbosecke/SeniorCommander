package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-09-09.
 */
@Entity
@Table(name = "chat_log")
public class ChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelConfiguration channel;

    @Column
    private String message;

    @ManyToOne
    @JoinColumn(name = "community_user_id")
    private CommunityUser communityUser;

    @Column
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ChannelConfiguration getChannel() {
        return channel;
    }

    public void setChannel(ChannelConfiguration channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CommunityUser getCommunityUser() {
        return communityUser;
    }

    public void setCommunityUser(CommunityUser communityUser) {
        this.communityUser = communityUser;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
