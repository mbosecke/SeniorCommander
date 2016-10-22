package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Created by mitch_000 on 2016-07-12.
 */
@Entity
@Table(name = "command_log")
public class CommandLogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "command_id")
    private CommandModel commandModel;

    @ManyToOne
    @JoinColumn(name = "community_user_id")
    private CommunityUserModel communityUserModel;

    @Column(name = "log_date")
    private ZonedDateTime logDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CommandModel getCommandModel() {
        return commandModel;
    }

    public void setCommandModel(CommandModel commandModel) {
        this.commandModel = commandModel;
    }

    public CommunityUserModel getCommunityUserModel() {
        return communityUserModel;
    }

    public void setCommunityUserModel(CommunityUserModel communityUserModel) {
        this.communityUserModel = communityUserModel;
    }

    public ZonedDateTime getLogDate() {
        return logDate;
    }

    public void setLogDate(ZonedDateTime logDate) {
        this.logDate = logDate;
    }
}
