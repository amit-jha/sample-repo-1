package com.aj.searchapi.controller;

import com.aj.searchapi.service.SearchService;
import com.aj.searchapi.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@RestController
public class SearchController {

    private SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }


    @GetMapping("/search")
    @CrossOrigin("http://localhost:3000")
    public Response search(@RequestParam("q")  String query, @RequestParam("page") int page) {
        return searchService.performSearch(query, page);
    }
}
