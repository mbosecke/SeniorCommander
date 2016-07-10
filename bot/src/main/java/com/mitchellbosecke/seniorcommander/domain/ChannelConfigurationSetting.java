package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;

/**
 * Created by mitch_000 on 2016-07-10.
 */
@Entity
@Table(name = "channel_setting")
public class ChannelConfigurationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelConfiguration channelConfiguration;

    @Column
    private String key;

    @Column
    private String value;

    public ChannelConfiguration getChannelConfiguration() {
        return channelConfiguration;
    }

    public void setChannelConfiguration(ChannelConfiguration channelConfiguration) {
        this.channelConfiguration = channelConfiguration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
