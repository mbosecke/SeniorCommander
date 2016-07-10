package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by mitch_000 on 2016-07-10.
 */
@Entity
@Table(name = "channel")
public class ChannelConfiguration {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @Column
    private String type;

    @OneToMany(mappedBy = "channelConfiguration")
    private Set<ChannelConfigurationSetting> settings;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<ChannelConfigurationSetting> getSettings() {
        return settings;
    }

    public void setSettings(Set<ChannelConfigurationSetting> settings) {
        this.settings = settings;
    }

    @Transient
    public String getSetting(String key) {
        String result = null;
        for (ChannelConfigurationSetting setting : settings) {
            if (key.equalsIgnoreCase(setting.getKey())) {
                result = setting.getValue();
            }
        }
        return result;
    }
}
