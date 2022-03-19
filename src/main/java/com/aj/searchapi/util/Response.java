package com.aj.searchapi.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
public class Response {

    @JsonProperty("searchTerm")
    private String searchTerm;

    @JsonProperty("total")
    private int total;

    @JsonProperty("articles")
    private List<Response.Article> articles;

    @Builder
    @Getter
    @ToString
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public final static class Article {

        @JsonProperty("_id")
        private String id;

        @JsonProperty("abstract")
        private String snippet;

        @JsonProperty("webUrl")
        private String website;

        @JsonProperty("listOfUrls")
        private List<String> urls;

        @JsonProperty("listOfTitles")
        private List<String> titles;

        @JsonProperty("listOfKeywords")
        private List<String> keywords;

        @JsonProperty("source")
        private String source;
    }

    public static class ArticleDb{
        private String document_id;
        private Article article;

        public ArticleDb() {
        }



    }
}
