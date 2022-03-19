package com.aj.searchapi.service;

import com.aj.searchapi.SearchManager;
import com.aj.searchapi.util.Response;
import com.aj.searchapi.util.UQL;
import org.springframework.stereotype.Service;

import javax.websocket.server.PathParam;

@Service
public class SearchService {

    private final SearchManager searchManager;

    public SearchService(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public Response performSearch(String query) {
        UQL uql = new UQL.Builder()
                .keyword(query)
                .build();
        return searchManager.broadcastSearch(uql);
    }

    public Response performSearch(String query, int page) {
        UQL uql = new UQL.Builder()
                .keyword(query)
                .page(page)
                .build();
        return searchManager.broadcastSearch(uql);
    }
}
