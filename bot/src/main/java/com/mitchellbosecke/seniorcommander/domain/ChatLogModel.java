package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Created by mitch_000 on 2016-09-09.
 */
@Entity
@Table(name = "chat_log")
public class ChatLogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelModel channel;

    @Column
    private String message;

    @ManyToOne
    @JoinColumn(name = "community_user_id")
    private CommunityUserModel communityUserModel;

    @Column
    private ZonedDateTime date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ChannelModel getChannel() {
        return channel;
    }

    public void setChannel(ChannelModel channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CommunityUserModel getCommunityUserModel() {
        return communityUserModel;
    }

    public void setCommunityUserModel(CommunityUserModel communityUserModel) {
        this.communityUserModel = communityUserModel;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
