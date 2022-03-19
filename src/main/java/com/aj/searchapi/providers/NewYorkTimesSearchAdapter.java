package com.aj.searchapi.providers;

import com.aj.searchapi.SearchApiConfiguration;
import com.aj.searchapi.util.SearchResult;
import com.aj.searchapi.util.UQO;
import com.aj.searchapi.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("NY")
@NoArgsConstructor
public class NewYorkTimesSearchAdapter implements Adapter {

    private SearchApiConfiguration configuration;

    @Autowired
    public NewYorkTimesSearchAdapter(SearchApiConfiguration configuration) {
        this.configuration = configuration;
    }

    private static Logger LOGGER = LoggerFactory.getLogger(NewYorkTimesSearchAdapter.class);
    private static final String CODE = "NY";
    private static final String KEY_FOR_API_URL = (CODE+"-api-url").toLowerCase();
    private static final String KEY_FOR_API_KEY = (CODE+"-api-key").toLowerCase();

    private static final String NAME = "New York Times";


    @Override
    public SearchResult search(UQO uqo) {
        URI uri = buildUri(uqo);
        HttpRequest request = Util.buildRequest(uri);
        String response = Util.send(request);
        ObjectMapper mapper = new ObjectMapper();
        //INFO: Required for mapping to Optional
        mapper.registerModule(new Jdk8Module());
        SearchResultDocument sr = null;
        try {
            sr = mapper.readValue(response, SearchResultDocument.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error while creating result object");
        }
        return mapToSearchResult(sr);

    }

    private URI buildUri(UQO uqo) {
        /*var URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        var API_KEY = "bt1jcIAtGnhG9EASWKEu45LWOAxFzndR";*/
        String url = configuration.getApi().get(KEY_FOR_API_URL);
        String key = configuration.getApi().get(KEY_FOR_API_KEY);
        return UriComponentsBuilder.fromUriString(url)
                .queryParam("api-key", key)
                .queryParam("q", uqo.getQ())
                .queryParam("page", uqo.getPage() == 0 ? 0 : uqo.getPage())
                .build()
                .toUri();
    }

    private SearchResult mapToSearchResult(SearchResultDocument sr) {
        SearchResult searchResult = new SearchResult();
        List<SearchResult.Article> articles = new ArrayList<>();
        for(SearchResultDocument.Response.Document document: sr.getResponse().getDocs()){
            SearchResult.Article article = SearchResult.Article.builder()
                    .id(document.getId())
                    .website(document.getWebUrl())
                    .keywords(document.getKeywords()
                            .stream()
                            .map(kw -> kw.getValue())
                            .collect(Collectors.toList()))
                    //INFO: Use of() static factory method with Optional.
                    // To ensure that if any of the headline is null the Supply a default value as "none"
                    .titles(List.of(
                            document.getHeadline().getMainHeadline().orElseGet(()->"none"),
                            document.getHeadline().getSecondaryHeadline().orElseGet(()->"none")))
                    .urls(List.of(
                            document.getWebUrl(),
                            document.getUri()))
                    .snippet(document.getSnippet())
                    .source(NAME)
                    .build();
            articles.add(article);
        }
        searchResult.setArticles(articles);
        searchResult.setTotal(sr.getResponse().getMeta().getTotalResults());
        return searchResult;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown=true)
    final static class SearchResultDocument{

        @JsonProperty("status")
        private String status;

        @JsonProperty("response")
        private Response response;


        @Getter
        @Setter
        final static class Response{

            @JsonProperty("docs")
            List<Document> docs;

            @JsonProperty("meta")
            private Meta meta;

            @Setter
            @Getter
            @JsonIgnoreProperties(ignoreUnknown=true)
            static final class Meta{

                @JsonProperty("hits")
                private int totalResults;
            }

            @Getter
            @Setter
            @JsonIgnoreProperties(ignoreUnknown=true)
            static class Document{

                @JsonProperty("_id")
                private String id;

                @JsonProperty("uri")
                private String uri;

                @JsonProperty("abstract")
                private String snippet;

                @JsonProperty("web_url")
                private String webUrl;

                @JsonProperty("headline")
                private Headline headline;

                @JsonProperty("keywords")
                private List<Keyword> keywords;

            }

            @Getter
            @Setter
            @JsonIgnoreProperties(ignoreUnknown=true)
            final static class Keyword{

                @JsonProperty("value")
                private String value;
            }

            @Getter
            @Setter
            @JsonIgnoreProperties(ignoreUnknown=true)
            final static class Headline{
                @JsonProperty("main")
                private Optional<String> mainHeadline;
                @JsonProperty("print_headline")
                private Optional<String> secondaryHeadline;
            }
        }
    }
}
