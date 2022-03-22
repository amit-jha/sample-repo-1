package com.aj.searchapi.providers;

import com.aj.searchapi.SearchApiConfiguration;
import com.aj.searchapi.exception.ApplicationException;
import com.aj.searchapi.util.SearchResult;
import com.aj.searchapi.util.UQO;
import com.aj.searchapi.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.swing.*;
import java.awt.font.MultipleMaster;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("TG")
@NoArgsConstructor
public class TheGuardianSearchAdapter implements Adapter {

    private static Logger LOGGER = LoggerFactory.getLogger(TheGuardianSearchAdapter.class);

    private SearchApiConfiguration configuration;

    @Autowired
    public TheGuardianSearchAdapter(SearchApiConfiguration configuration) {
        this.configuration = configuration;
    }

    private static final String CODE = "TG";
    private static final String NAME = "The Guardian";
    private static final String KEY_FOR_API_URL = (CODE+"-api-url").toLowerCase();
    private static final String KEY_FOR_API_KEY = (CODE+"-api-key").toLowerCase();



    @Override
    @CircuitBreaker(name = "tgCircuitBreaker")
    public SearchResult search(UQO uqo) throws ApplicationException {
        var uri = buildUri(uqo);
        var request = Util.buildRequest(uri);
        String response = null;
        SearchResultDocument sr = null;
        var mapper = new ObjectMapper();
        //INFO: Following snippet enables the use of Java8 features at time on JSON->JavaObject mapping
        mapper.registerModule(new Jdk8Module());
        try {
            response = Util.send(request);
            sr = mapper.readValue(response, SearchResultDocument.class);
        } catch (ApplicationException e) {
            LOGGER.error(String.format("%s - %s", "Error occurred while API call", e.getMessage()));
            throw new ApplicationException(e);
        } catch (JsonProcessingException e) {
            LOGGER.error(String.format("%s - %s", "Error while creating search result object", e.getMessage()));
            throw new ApplicationException(e);
        }
        return mapToSearchResult(sr);

    }

    private URI buildUri(UQO uqo) {
        String url = configuration.getApi().get(KEY_FOR_API_URL);
        String key = configuration.getApi().get(KEY_FOR_API_KEY);
        return UriComponentsBuilder.fromUriString(url)
                .queryParam("api-key", key)
                .queryParam("q", uqo.getQ())
                .queryParam("show-tags", "keyword")
                .queryParam("show-fields", "headline")
                .queryParam("page", uqo.getPage() == 0 ? 1 : uqo.getPage())
                .build()
                .toUri();
    }

    private SearchResult mapToSearchResult(SearchResultDocument sr) {
        SearchResult searchResult = new SearchResult();
        List<SearchResult.Article> articles = new ArrayList<>();
        for(SearchResultDocument.Response.Result result: sr.getResponse().getResults()){
            var article = SearchResult.Article.builder()
                    .id(result.getId())
                    .website(result.getWebUrl())
                    .keywords(result.getKeywords()
                            .stream()
                            .map(kw -> kw.getName())
                            .collect(Collectors.toList()))
                     //INFO: Use of() static factory method with Optional.
                    // To ensure that if any of the headline is null the Supply a default value as "none"
                    .titles(List.of(
                            result.getTitle(),
                            result.getFields().getHeadline().orElseGet(() ->"none")))
                    .urls(List.of(
                            result.getWebUrl(),
                            result.getApiUrl()))
                    .source(NAME)
                    .build();
            articles.add(article);
        }
        searchResult.setArticles(articles);
        searchResult.setTotal(sr.getResponse().getTotal());
        return searchResult;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown=true)
    final static class SearchResultDocument{

        @JsonProperty("response")
        private Response response;


        @Setter
        @Getter
        @JsonIgnoreProperties(ignoreUnknown=true)
        final static class Response{

            @JsonProperty("status")
            private String status;

            @JsonProperty("total")
            private int total;

            @JsonProperty("results")
            private List<Result> results;

            @Setter
            @Getter
            @JsonIgnoreProperties(ignoreUnknown=true)
            final static class Result{
                @JsonProperty("id")
                private String id;

                @JsonProperty("webTitle")
                private String title;

                @JsonProperty("webUrl")
                private String webUrl;

                @JsonProperty("apiUrl")
                private String apiUrl;

                @JsonProperty("fields")
                private Field fields;

                @JsonProperty("tags")
                private List<Keyword> keywords;

                @Setter
                @Getter
                @JsonIgnoreProperties(ignoreUnknown=true)
                final static class Field{
                    @JsonProperty("headline")
                    private Optional<String> headline;
                }

                @Setter
                @Getter
                @JsonIgnoreProperties(ignoreUnknown=true)
                final static class Keyword{
                    @JsonProperty("webTitle")
                    private String name;
                }
            }
        }
    }
}
