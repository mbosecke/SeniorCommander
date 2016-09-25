package com.mitchellbosecke.seniorcommander.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by mitch_000 on 2016-09-24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelFollow {

    private Date createdAt;

    private boolean notifications;

    private User user;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
