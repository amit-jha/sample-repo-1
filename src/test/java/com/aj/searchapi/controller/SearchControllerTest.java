package com.aj.searchapi.controller;

import com.aj.searchapi.exception.ApplicationException;
import com.aj.searchapi.service.SearchService;
import com.aj.searchapi.util.Response;
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
class SearchControllerTest {

    @Mock
    SearchService searchService;

    @InjectMocks
    SearchController searchController;

    @Mock
    Response response;

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
    void search() throws ApplicationException {
        when(searchService.performSearch("apple", 0)).thenReturn(response);
        searchController.search("apple", 0);
        assertEquals(response.getTotal(), 1);

    }
}