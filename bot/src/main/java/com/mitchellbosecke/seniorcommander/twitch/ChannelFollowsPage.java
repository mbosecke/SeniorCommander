package com.mitchellbosecke.seniorcommander.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by mitch_000 on 2016-09-24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelFollowsPage {

    @JsonProperty("_total")
    private int total;

    @JsonProperty("_cursor")
    private String cursor;

    private List<ChannelFollow> follows;

    private String channel;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ChannelFollow> getFollows() {
        return follows;
    }

    public void setFollows(List<ChannelFollow> follows) {
        this.follows = follows;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public ChannelFollowsPage next() {
        return new TwitchApi().followers(channel, cursor);
    }
}
