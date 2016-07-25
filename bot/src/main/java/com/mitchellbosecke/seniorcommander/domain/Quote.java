package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-17.
 */
@Entity
@Table(name = "quote")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @Column(name = "community_sequence")
    private long communitySequence;

    @Column
    private String author;

    @Column
    private String content;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCommunitySequence() {
        return communitySequence;
    }

    public void setCommunitySequence(long communitySequence) {
        this.communitySequence = communitySequence;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
