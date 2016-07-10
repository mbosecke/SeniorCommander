package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by mitch_000 on 2016-07-09.
 */
@Entity
@Table(name = "community")
public class Community {

    @Id
    @Column
    private long id;

    @Column
    private String name;

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
}
