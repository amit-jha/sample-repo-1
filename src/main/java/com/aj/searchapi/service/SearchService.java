package com.aj.searchapi.service;

import com.aj.searchapi.SearchManager;
import com.aj.searchapi.exception.ApplicationException;
import com.aj.searchapi.providers.NewYorkTimesSearchAdapter;
import com.aj.searchapi.util.Response;
import com.aj.searchapi.util.UQL;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.server.PathParam;

@Service
public class SearchService {

    private static Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    private final SearchManager searchManager;

    public SearchService(SearchManager searchManager) {
        this.searchManager = searchManager;
    }


    public Response performSearch(String query) throws ApplicationException {
        return performSearch(query, 0);
    }

    public Response performSearch(String query, int page) throws ApplicationException {
        UQL uql = new UQL.Builder()
                .keyword(query)
                .page(page)
                .build();
            return searchManager.broadcastSearch(uql);

    }
}
