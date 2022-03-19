package com.aj.searchapi.providers;

import com.aj.searchapi.SearchApiConfiguration;
import com.aj.searchapi.SearchManager;
import com.aj.searchapi.util.SearchResult;
import com.aj.searchapi.util.UQL;
import com.aj.searchapi.util.UQO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TheGuardianSearchAdapterTest {
    @Mock
    SearchApiConfiguration configuration;


    @InjectMocks
    TheGuardianSearchAdapter ad;


    @Test
    void search() {
        UQL uql = new UQL.Builder().keyword("apple").build();
        UQO uqo = new UQO(uql);
        when(configuration.getApi()).thenReturn(Map.of(
                "tg-api-url", "https://content.guardianapis.com/search",
                "tg-api-key", "99ef632c-d381-4bb8-92cd-d315021c7110")
        );
        SearchResult sr = ad.search(uqo);
        assertEquals(sr.getArticles().size(), 10);
    }
}