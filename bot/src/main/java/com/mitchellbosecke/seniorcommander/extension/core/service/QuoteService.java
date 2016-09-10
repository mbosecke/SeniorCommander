package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.QuoteModel;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface QuoteService extends BaseService {

    QuoteModel addQuote(CommunityModel communityModel, String author, String content);

    QuoteModel findQuote(CommunityModel communityModel, long communitySequenceId);

    QuoteModel findRandomQuote(CommunityModel communityModel);

    QuoteModel findRandomQuote(CommunityModel communityModel, String author);

}
