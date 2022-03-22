package com.aj.searchapi;

import com.aj.searchapi.exception.ApplicationException;
import com.aj.searchapi.providers.Adapter;
import com.aj.searchapi.providers.Provider;
import com.aj.searchapi.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

;

@Component
@NoArgsConstructor
public class SearchManager implements ApplicationContextAware {

    private static Logger LOGGER = LoggerFactory.getLogger(SearchManager.class);

    private SearchApiConfiguration configuration;
    private Util.Store store;

    @Autowired
    public SearchManager(SearchApiConfiguration configuration, Util.Store store) {
        this.configuration = configuration;
        this.store = store;
    }


    public Response broadcastSearch(UQL uql) throws ApplicationException {
        List<Adapter> adapters = getSearchAdapters();
        var srs = adapters.stream()
                .map(searchAdapter -> {
                    SearchResult sr = null;
                    try {
                        sr = getSearchResult(uql, searchAdapter);
                    } catch (ApplicationException e) {
                        e.printStackTrace();
                    }
                    return sr;
                })
                .collect(Collectors.toList());
        Response mergedResponse = merger(srs);
        mergedResponse.setSearchTerm(uql.getKeyword());

        //INFO: Storing results in another thread.
        Runnable r = () -> store.store(mergedResponse);
        new Thread(r).start();

        return mergedResponse;
    }

    private SearchResult getSearchResult(UQL uql, Adapter searchAdapter) throws ApplicationException {
        return searchAdapter
               .search(UQO.builder()
                       .q(uql.getKeyword())
                       .page(uql.getPage())
                       .build());
    }

    private Response merger(List<SearchResult> srs) {
        var total = srs.stream().collect(Collectors.summingInt(sr -> sr.getTotal()));
        var articles = srs.stream()
            .flatMap(searchResult -> {
                return searchResult.getArticles().stream();
            }).map(article -> {
                    return Response.Article.builder()
                            .id(article.getId())
                            .titles(article.getTitles()
                                    .stream()
                                    .filter(t -> !t.equals("none"))
                                    .collect(Collectors.toList()))
                            .keywords(article.getKeywords())
                            .urls(article.getUrls())
                            .snippet(article.getSnippet())
                            .website(article.getWebsite())
                            .source(article.getSource())
                            .build();
                })
                .collect(Collectors.toList());
        Response response = Response.builder().articles(articles).total(total).build();
        return response;
    }

    public List<Provider> getSearchProviders() throws ApplicationException {
        final File SEARCH_PROVIDERS_FILE = Util.getResource(configuration.getStoreLocation(),configuration.getSearchProvider());
        List<Provider> providers = null;
        if (SEARCH_PROVIDERS_FILE.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                providers = Arrays.asList(mapper.readValue(SEARCH_PROVIDERS_FILE, Provider[].class));
            } catch (IOException e) {
                LOGGER.error("Error while mapping JSON to provider");
                TODO: throw new ApplicationException(e);
            }
        }else{
            throw new ApplicationException(SEARCH_PROVIDERS_FILE.getName()+ " does not exist");
        }
        return providers;
    }

    public List<Adapter> getSearchAdapters() throws ApplicationException{
        List<Provider> providers = getSearchProviders();
        List<Adapter> adapters = null;
        if (null != providers) {
            try {
                adapters = providers.stream()
                        .filter(provider -> provider.active())
                        .map(provider -> (Adapter) applicationContext.getBean(provider.code()))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                LOGGER.error("Error while mapping adapters to a provider");
                throw new ApplicationException(e);
            }
        }else{
            throw new ApplicationException("Error while reading provider list");
        }
        return adapters;
    }


    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}