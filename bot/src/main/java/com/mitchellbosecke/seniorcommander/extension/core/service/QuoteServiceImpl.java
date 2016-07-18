package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.Quote;
import org.hibernate.SessionFactory;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class QuoteServiceImpl extends BaseServiceImpl implements QuoteService {

    public QuoteServiceImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Quote addQuote(Community community, String author, String content) {
        Quote quote = new Quote();
        quote.setCommunity(community);
        quote.setAuthor(author);
        quote.setContent(content);
        persist(quote);
        return quote;
    }

}
