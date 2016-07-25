package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.Quote;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class QuoteServiceImpl extends BaseServiceImpl implements QuoteService {

    public QuoteServiceImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Quote addQuote(Community community, String author, String content) {
        Long communitySequence;

        communitySequence = sessionFactory.getCurrentSession()
                .createQuery("SELECT max(q.communitySequence) FROM Quote q WHERE q.community = :community", Long.class)
                .setParameter("community", community).getSingleResult();

        communitySequence = communitySequence == null ? 0 : communitySequence;

        communitySequence++;

        Quote quote = new Quote();
        quote.setCommunity(community);
        quote.setAuthor(author);
        quote.setContent(content);
        quote.setCommunitySequence(communitySequence);
        quote.setCreatedDate(new Date());
        persist(quote);
        return quote;
    }

    @Override
    public Quote findQuote(Community community, long communitySequenceId) {
        Quote quote = null;

        try {
            quote = sessionFactory.getCurrentSession()
                    .createQuery("SELECT q FROM Quote q WHERE q.community = :community AND q.communitySequence = :sequence", Quote.class)
                    .setParameter("community", community).setParameter("sequence", communitySequenceId)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }

        return quote;
    }

    @Override
    public Quote findRandomQuote(Community community) {
        Quote quote = null;

        try {
            quote = sessionFactory.getCurrentSession()
                    .createQuery("SELECT q FROM Quote q WHERE q.community = :community ORDER BY rand()", Quote.class)
                    .setParameter("community", community).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex) {
        }

        return quote;
    }

    @Override
    public Quote findRandomQuote(Community community, String author) {
        Quote quote = null;

        try {
            quote = sessionFactory.getCurrentSession()
                    .createQuery("SELECT q FROM Quote q WHERE q.community = :community AND lower(q.author) = lower(:author) ORDER BY rand()", Quote.class)
                    .setParameter("community", community).setParameter("author", author).setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }

        return quote;
    }
}
