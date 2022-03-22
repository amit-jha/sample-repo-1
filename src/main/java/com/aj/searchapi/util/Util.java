package com.aj.searchapi.util;

import com.aj.searchapi.SearchApiConfiguration;
import com.aj.searchapi.exception.ApplicationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class Util {

    private static Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static HttpRequest buildRequest(URI uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .build();
        return httpRequest;
    }

    public static String send(HttpRequest request) throws ApplicationException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new ApplicationException(e);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            throw new ApplicationException(e);
        }
        return response.body();
    }

    public static File getResource(String location, String resourceName){
        File f = null;
        var absLocation = location.concat("/").concat(resourceName);
        f = Paths.get(absLocation).toFile();
        return f;
    }

    /***
     * INFO
     * Algorithm: Write Path & Read Path
     * Step1 - Create a data store
     *  1. Each document will be given a UUID
     *  2. Map each compressed document against document_id [Not implemented Compression]
     *  3. Store it as JSON.
     * Step2 - Create an index [Using title for brevity]
     * 1. Remove stop words from title
     * 2. Map each remaining word with document id
     * 3. Store it as JSON.
     * Step3 - Store optimization [Not implemented]
     * 1. Compaction - merge small index to a big index.
     *
     */

    @Component
    @NoArgsConstructor
    public static class Store{

        private static Logger LOGGER = LoggerFactory.getLogger(Util.Store.class);

        private SearchApiConfiguration configuration;
        Index index;

        @Autowired
        public Store(SearchApiConfiguration configuration, Index index) {
            this.configuration = configuration;
            this.index = index;
        }

        public void store(Response response){
            final File STORE_DB_FILE = Util.getResource(configuration.getStoreLocation(), configuration.getArticleDbLocation());
            Map<String, Response.Article> store = new HashMap<>();
            List<Map<String, Response.Article>> db = response.getArticles()
                    .stream()
                    .parallel()
                    .map(article -> {
                        String document_id = UUID.randomUUID().toString();
                        return Map.of(document_id, article);
                    }).collect(Collectors.toList());

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                if(!STORE_DB_FILE.exists()) {
                    STORE_DB_FILE.createNewFile();
                }else{
                    List<Map<String, Response.Article>> a = objectMapper.readValue(STORE_DB_FILE, new TypeReference<List<Map<String, Response.Article>>>(){});
                    db.addAll(a);
                }
                objectMapper.writeValue(STORE_DB_FILE, db);
                index.build(db);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                //TODO: throw ApplicationException(e)
            }
        }
    }

    @Component
    @NoArgsConstructor
    public static class Index{
        private static Logger LOGGER = LoggerFactory.getLogger(Util.Index.class);
        private SearchApiConfiguration configuration;

        @Autowired
        public Index(SearchApiConfiguration configuration) {
            this.configuration = configuration;
        }

        public void build(List<Map<String, Response.Article>> db) {
            File INDEX_FILE = Util.getResource(configuration.getStoreLocation(), configuration.getIndexLocation());
            db.stream().forEach(map ->{
               map.entrySet().stream().forEach(entry ->{
                   var docId = entry.getKey();
                   var title = entry.getValue().getTitles().get(0);
                   var index = stopWordsCleansing(title)
                           .stream()
                           .map(word -> Map.of(word, docId)).collect(Collectors.toList());
                   try {
                       var objectMapper = new ObjectMapper();
                       if(!INDEX_FILE.exists()) {
                           INDEX_FILE.createNewFile();
                       } else {
                           var a = objectMapper.readValue(INDEX_FILE, new TypeReference<List<Map<String, String>>>() {
                           });
                           index.addAll(a);
                       }
                       objectMapper.writeValue(INDEX_FILE, index);
                   }catch (Exception e){
                       e.printStackTrace();
                       LOGGER.error(e.getMessage());
                       //TODO: throw
                   }
               });
            });
        }


        private List<String> stopWordsCleansing(String title) {
            final File STOP_WORDS = Util.getResource(configuration.getStoreLocation(),configuration.getStopWordsLocation());
            List<String> stopWords = null;
            try {
                stopWords = Files.readAllLines(STOP_WORDS.toPath());
                List<String> upperCase = stopWords.stream().map(word -> word.toUpperCase()).collect(Collectors.toList());
                List<String> initialUpperCase = stopWords.stream().map(str -> str.substring(0, 1).toUpperCase() + str.substring(1)).collect(Collectors.toList());

                stopWords.addAll(upperCase);
                stopWords.addAll(initialUpperCase);

            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
            List<String> words = Arrays.stream(title.split(" ")).collect(Collectors.toList());
            words.removeAll(stopWords);
            return words;

        }


    }
}
