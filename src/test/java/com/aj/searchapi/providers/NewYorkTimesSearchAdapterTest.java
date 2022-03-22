package com.aj.searchapi.providers;

import com.aj.searchapi.SearchApiConfiguration;
import com.aj.searchapi.SearchManager;
import com.aj.searchapi.exception.ApplicationException;
import com.aj.searchapi.util.SearchResult;
import com.aj.searchapi.util.UQL;
import com.aj.searchapi.util.UQO;
import com.aj.searchapi.util.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewYorkTimesSearchAdapterTest {

    @Mock
    SearchApiConfiguration configuration;


    @InjectMocks
    NewYorkTimesSearchAdapter ad;


    @Test
    void search() throws ApplicationException {
        UQL uql = new UQL.Builder().keyword("apple").build();
        UQO uqo = new UQO(uql);
        when(configuration.getApi()).thenReturn(Map.of(
                "ny-api-url", "https://api.nytimes.com/svc/search/v2/articlesearch.json",
                "ny-api-key", "bt1jcIAtGnhG9EASWKEu45LWOAxFzndR")
        );
        SearchResult sr = ad.search(uqo);
        assertEquals(sr.getArticles().size(), 10);
    }
}