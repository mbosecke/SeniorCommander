package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.Quote;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface QuoteService extends BaseService {

    Quote addQuote(Community community, String author, String content);



}
