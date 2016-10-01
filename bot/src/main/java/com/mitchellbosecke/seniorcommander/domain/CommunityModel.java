package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by mitch_000 on 2016-07-09.
 */
@Entity
@Table(name = "community")
public class CommunityModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "communityModel")
    private Set<ChannelModel> channelModels;

    @OneToOne(mappedBy = "communityModel")
    private BettingGameModel bettingGameModel;

    @OneToMany(mappedBy = "communityModel")
    private Set<CommunitySettingModel> settings;

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

    public Set<ChannelModel> getChannelModels() {
        return channelModels;
    }

    public void setChannelModels(Set<ChannelModel> channelModels) {
        this.channelModels = channelModels;
    }

    public BettingGameModel getBettingGameModel() {
        return bettingGameModel;
    }

    public void setBettingGameModel(BettingGameModel bettingGameModel) {
        this.bettingGameModel = bettingGameModel;
    }

    public Set<CommunitySettingModel> getSettings() {
        return settings;
    }

    public void setSettings(Set<CommunitySettingModel> settings) {
        this.settings = settings;
    }

    @Transient
    public String getSetting(String key) {
        String result = null;
        for (CommunitySettingModel setting : settings) {
            if (key.equalsIgnoreCase(setting.getKey())) {
                result = setting.getValue();
            }
        }
        return result;
    }
}
