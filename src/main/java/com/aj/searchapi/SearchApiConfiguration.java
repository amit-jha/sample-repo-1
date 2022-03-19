package com.aj.searchapi;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "article-finder")
@Setter
@Getter
@ToString
public class SearchApiConfiguration {
    private String storeLocation;
    private String indexLocation;
    private String stopWordsLocation;
    private String articleDbLocation;
    private String searchProvider;


    private Map<String, String> api;

}
