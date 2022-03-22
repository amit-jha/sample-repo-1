package com.aj.searchapi.controller;

import com.aj.searchapi.exception.ApplicationException;
import com.aj.searchapi.providers.TheGuardianSearchAdapter;
import com.aj.searchapi.service.SearchService;
import com.aj.searchapi.util.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

/**
 * This class is responsible for initiating search for articles for a given query. It delegates users request to
 * see {@link SearchService SearchService}
 */
@RestController
public class SearchController {

    private static Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    @Operation(summary = "Get news articles based on query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "News articles retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class )) }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "News articles not found",
                    content = @Content) })

    @GetMapping("/search")
    @CrossOrigin("http://localhost:3000")
    public Response search(@RequestParam("q")  String query, @RequestParam("page") int page) {
        try {
            return searchService.performSearch(query, page);
        } catch (ApplicationException e) {
            return Response.builder().status("Error").build();
        }
    }

    @GetMapping("/")
    public ResponseEntity health() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.builder().status("UP").build());

    }
}
