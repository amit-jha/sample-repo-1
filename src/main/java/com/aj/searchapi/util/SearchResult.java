package com.aj.searchapi.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class SearchResult {
    @JsonProperty("total")
    private int total;

    @JsonProperty("articles")
    private List<Article> articles;

    @Builder
    @Getter
    @ToString
    public final static class Article {
        private String id;
        private String snippet;
        private String website;
        private List<String> urls;
        private List<String> titles;
        private List<String> keywords;
        private String source;
    }

}
