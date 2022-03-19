package com.aj.searchapi;

import com.aj.searchapi.providers.Adapter;
import com.aj.searchapi.providers.NewYorkTimesSearchAdapter;
import com.aj.searchapi.providers.Provider;
import com.aj.searchapi.util.Response;
import com.aj.searchapi.util.UQL;
import com.aj.searchapi.util.UQO;
import com.aj.searchapi.util.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchManagerTest {

    @Mock
    SearchApiConfiguration configuration;

    @Mock
    Util.Store store;

    @InjectMocks
    SearchManager searchManager;

    @Mock
    ApplicationContext applicationContext;


    private static String DIR = "/Users/amijha0/repos/SampleSearchApi/src/test/test-store";

   /* @BeforeEach
    void setUp(){
        when(configuration.getStoreLocation()).thenReturn(DIR);
        when(configuration.getSearchProvider()).thenReturn("search-providers.json");
        searchManager.setApplicationContext(applicationContext);
        when(applicationContext.getBean("NY")).thenReturn(new NewYorkTimesSearchAdapter(configuration));

        when(configuration.getArticleDbLocation()).thenReturn("store-db.json");
        when(configuration.getIndexLocation()).thenReturn("aticles-index.json");
        when(configuration.getStopWordsLocation()).thenReturn("stopwords.json");

        when(configuration.getApi()).thenReturn(Map.of(
                "ny-api-url", "https://api.nytimes.com/svc/search/v2/articlesearch.json",
                "ny-api-key", "bt1jcIAtGnhG9EASWKEu45LWOAxFzndR")
        );
    }
*/
    @Test
    void ListSearchProviders() {
        when(configuration.getStoreLocation()).thenReturn(DIR);
        when(configuration.getSearchProvider()).thenReturn("search-providers.json");
        List<Provider> lp = searchManager.getSearchProviders();
        assertTrue(lp.size() == 2);
    }

    @Test
    void getPublisherCode() {
        when(configuration.getStoreLocation()).thenReturn(DIR);
        when(configuration.getSearchProvider()).thenReturn("search-providers.json");
        List<Provider> lp = searchManager.getSearchProviders();
        assertTrue(lp.stream().anyMatch(p -> p.code().equals("NY")));
    }

    @Test
    void getPublisherAdapter() {
        when(configuration.getStoreLocation()).thenReturn(DIR);
        when(configuration.getSearchProvider()).thenReturn("search-providers.json");
        searchManager.setApplicationContext(applicationContext);
        when(applicationContext.getBean("NY")).thenReturn(new NewYorkTimesSearchAdapter(configuration));
        List<Adapter> lp = searchManager.getSearchAdapters();
        assertInstanceOf(NewYorkTimesSearchAdapter.class, lp.get(0));
    }

    @Test
    void searchResult() {
        UQL uql = new UQL.Builder().keyword("apple").build();
        UQO uqo = new UQO(uql);

        when(configuration.getStoreLocation()).thenReturn(DIR);
        when(configuration.getSearchProvider()).thenReturn("search-providers.json");
        searchManager.setApplicationContext(applicationContext);
        when(applicationContext.getBean("NY")).thenReturn(new NewYorkTimesSearchAdapter(configuration));
        when(configuration.getApi()).thenReturn(Map.of(
                "ny-api-url", "https://api.nytimes.com/svc/search/v2/articlesearch.json",
                "ny-api-key", "bt1jcIAtGnhG9EASWKEu45LWOAxFzndR")
        );

        Response res = searchManager.broadcastSearch(uql);
        assertEquals(res.getArticles().size(), 10);
    }

    @Test
    void storeTest(){
        Response.Article article = Response.Article.builder()
                .id("123")
                .source("TestSource")
                .snippet("TestSnippet")
                .urls(List.of("URL"))
                .website("TestWebSite")
                .titles(List.of("Aria revokes former Sony Music boss Denis Handlin’s icon award"))
                .keywords(List.of("Keywords"))
                .build();

        Response.Article article1 = Response.Article.builder()
                .id("123")
                .source("TestSource")
                .snippet("TestSnippet")
                .urls(List.of("URL"))
                .website("TestWebSite")
                .titles(List.of("The Tech Gifts That Are Hard to Buy This Holiday Season"))
                .keywords(List.of("Keywords"))
                .build();

        Response response = Response.builder()
                .searchTerm("ABC")
                .total(1)
                .articles(List.of(article, article1))
                .build();

        store.store(response);




    }
}