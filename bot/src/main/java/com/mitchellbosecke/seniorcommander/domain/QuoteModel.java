package com.mitchellbosecke.seniorcommander.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Created by mitch_000 on 2016-07-17.
 */
@Entity
@Table(name = "quote")
public class QuoteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityModel communityModel;

    @Column(name = "community_sequence")
    private long communitySequence;

    @Column
    private String author;

    @Column
    private String content;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CommunityModel getCommunityModel() {
        return communityModel;
    }

    public void setCommunityModel(CommunityModel communityModel) {
        this.communityModel = communityModel;
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

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
