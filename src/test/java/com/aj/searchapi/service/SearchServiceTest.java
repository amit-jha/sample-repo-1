package com.aj.searchapi.service;

import com.aj.searchapi.SearchManager;
import com.aj.searchapi.exception.ApplicationException;
import com.aj.searchapi.util.Response;
import com.aj.searchapi.util.UQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    SearchManager searchManager;

    @InjectMocks
    SearchService searchService;

    @Mock
    Response response;

    @Mock
    UQL uql;

    @BeforeEach
    void setup(){
        Response.Article article = Response.Article.builder()
                .id("123")
                .source("TestSource")
                .snippet("TestSnippet")
                .urls(List.of("URL"))
                .website("TestWebSite")
                .titles(List.of("Aria revokes former Sony Music boss Denis Handlin’s icon award"))
                .keywords(List.of("Keywords"))
                .build();

        response = Response.builder()
                .searchTerm("ABC")
                .total(1)
                .articles(List.of(article))
                .build();
    }

    @Test
    void performSearch() throws ApplicationException {
        searchService.performSearch("apple");
        assertEquals(response.getTotal(), 1);
    }
}