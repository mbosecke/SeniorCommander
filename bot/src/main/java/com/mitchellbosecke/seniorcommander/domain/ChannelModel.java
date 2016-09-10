package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by mitch_000 on 2016-07-10.
 */
@Entity
@Table(name = "channel")
public class ChannelModel {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityModel communityModel;

    @Column
    private String type;

    @OneToMany(mappedBy = "channelModel")
    private Set<ChannelSettingModel> settings;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "online_channel_user", joinColumns = @JoinColumn(name = "channel_id"), inverseJoinColumns = @JoinColumn(name = "community_user_id"))
    private Set<CommunityUserModel> onlineUsers;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<ChannelSettingModel> getSettings() {
        return settings;
    }

    public void setSettings(Set<ChannelSettingModel> settings) {
        this.settings = settings;
    }

    public Set<CommunityUserModel> getOnlineUsers() {
        return onlineUsers;
    }

    @Transient
    public String getSetting(String key) {
        String result = null;
        for (ChannelSettingModel setting : settings) {
            if (key.equalsIgnoreCase(setting.getKey())) {
                result = setting.getValue();
            }
        }
        return result;
    }
}
