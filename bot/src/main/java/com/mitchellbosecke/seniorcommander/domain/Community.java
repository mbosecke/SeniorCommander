package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by mitch_000 on 2016-07-09.
 */
@Entity
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String name;

    @OneToMany(mappedBy="community")
    private Set<ChannelConfiguration> channelConfigurations;

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

    public Set<ChannelConfiguration> getChannelConfigurations() {
        return channelConfigurations;
    }

    public void setChannelConfigurations(Set<ChannelConfiguration> channelConfigurations) {
        this.channelConfigurations = channelConfigurations;
    }
}
