package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.QuoteModel;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class QuoteService extends BaseService {

    public QuoteService(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public QuoteModel addQuote(CommunityModel communityModel, String author, String content) {
        Long communitySequence;

        communitySequence = sessionFactory.getCurrentSession()
                .createQuery("SELECT max(q.communitySequence) FROM QuoteModel q WHERE q.communityModel = :community",
                        Long
                                .class)
                .setParameter("community", communityModel).getSingleResult();

        communitySequence = communitySequence == null ? 0 : communitySequence;

        communitySequence++;

        QuoteModel quoteModel = new QuoteModel();
        quoteModel.setCommunityModel(communityModel);
        quoteModel.setAuthor(author);
        quoteModel.setContent(content);
        quoteModel.setCommunitySequence(communitySequence);
        quoteModel.setCreatedDate(new Date());
        persist(quoteModel);
        return quoteModel;
    }

    public QuoteModel findQuote(CommunityModel communityModel, long communitySequenceId) {
        QuoteModel quoteModel = null;

        try {
            quoteModel = sessionFactory.getCurrentSession()
                    .createQuery("SELECT q FROM QuoteModel q WHERE q.communityModel = :community AND q" +
                            ".communitySequence =" +
                            " :sequence", QuoteModel.class)
                    .setParameter("community", communityModel).setParameter("sequence", communitySequenceId)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }

        return quoteModel;
    }

    public QuoteModel findRandomQuote(CommunityModel communityModel) {
        QuoteModel quoteModel = null;

        try {
            quoteModel = sessionFactory.getCurrentSession()
                    .createQuery("SELECT q FROM QuoteModel q WHERE q.communityModel = :community ORDER BY rand()",
                            QuoteModel.class)
                    .setParameter("community", communityModel).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex) {
        }

        return quoteModel;
    }

    public QuoteModel findRandomQuote(CommunityModel communityModel, String author) {
        QuoteModel quoteModel = null;

        try {
            quoteModel = sessionFactory.getCurrentSession()
                    .createQuery("SELECT q FROM QuoteModel q WHERE q.communityModel = :community AND lower(q.author) " +
                            "= lower(:author) ORDER BY rand()", QuoteModel.class)
                    .setParameter("community", communityModel).setParameter("author", author).setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }

        return quoteModel;
    }

}
