package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.QuoteModel;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;

import javax.persistence.NoResultException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class QuoteService extends BaseService {

    public QuoteModel addQuote(CommunityModel communityModel, String author, String content) {
        Long communitySequence;

        communitySequence = TransactionManager.getCurrentSession()
                .createQuery("SELECT max(q.communitySequence) FROM QuoteModel q WHERE q.communityModel = :community", Long.class)
                .setParameter("community", communityModel).getSingleResult();

        communitySequence = communitySequence == null ? 0 : communitySequence;

        communitySequence++;

        QuoteModel quoteModel = new QuoteModel();
        quoteModel.setCommunityModel(communityModel);
        quoteModel.setAuthor(author);
        quoteModel.setContent(content);
        quoteModel.setCommunitySequence(communitySequence);
        quoteModel.setCreatedDate(ZonedDateTime.now(ZoneId.of("UTC")));
        persist(quoteModel);
        return quoteModel;
    }

    public QuoteModel findQuote(CommunityModel communityModel, long communitySequenceId) {
        QuoteModel quoteModel = null;

        try {
            //@formatter:off
            quoteModel = TransactionManager.getCurrentSession()
                    .createQuery("" +
                            "SELECT q " +
                            "FROM QuoteModel q " +
                            "WHERE q.communityModel = :community " +
                            "AND q.communitySequence = :sequence", QuoteModel.class)
                    .setParameter("community", communityModel).setParameter("sequence", communitySequenceId)
                    .getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
        }

        return quoteModel;
    }

    public QuoteModel findRandomQuote(CommunityModel communityModel) {
        QuoteModel quoteModel = null;

        try {
            //@formatter:off
            quoteModel = TransactionManager.getCurrentSession()
                    .createQuery("" +
                                    "SELECT q " +
                                    "FROM QuoteModel q " +
                                    "WHERE q.communityModel = :community " +
                                    "ORDER BY rand()",
                            QuoteModel.class)
                    .setParameter("community", communityModel).setMaxResults(1).getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
        }

        return quoteModel;
    }

    public QuoteModel findRandomQuote(CommunityModel communityModel, String author) {
        QuoteModel quoteModel = null;

        try {
            //@formatter:off
            quoteModel = TransactionManager.getCurrentSession()
                    .createQuery("SELECT q " +
                            "FROM QuoteModel q " +
                            "WHERE q.communityModel = :community " +
                            "AND lower(q.author) = lower(:author) " +
                            "ORDER BY rand()", QuoteModel.class)
                    .setParameter("community", communityModel).setParameter("author", author).setMaxResults(1)
                    .getSingleResult();
            //@formatter:on
        } catch (NoResultException ex) {
        }

        return quoteModel;
    }

}
