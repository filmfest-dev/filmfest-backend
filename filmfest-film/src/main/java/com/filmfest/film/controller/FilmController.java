package com.filmfest.film.controller;

import com.filmfest.film.service.DatabaseAdminService;
import com.filmfest.film.service.FilmDetailScraperService;
import com.filmfest.film.service.FilmUrlScraperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmUrlScraperService filmUrlScraperService;
    private final FilmDetailScraperService filmDetailScraperService;
    private final DatabaseAdminService databaseAdminService;

    @Operation(
            summary = "Import films for a given year from the Sitges Film Festival",
            description = "Fetches film URLs for the specified year from the Sitges Film Festival website, search detailed metadata (title, synopsis, duration, etc.), and saves the results to MongoDB."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search and saving process completed successfully"),
            @ApiResponse(responseCode = "500", description = "Unexpected error during search or persistence")
    })
    @PostMapping(value = "/imports/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> scrapeAndSaveAll(@PathVariable int year) {
        return filmUrlScraperService.scrapeAndSaveFilmUrls(year)
                .then(filmDetailScraperService.scrapeAndSaveFilmDetails());
    }

    @Operation(
            summary = "Clear films database",
            description = "Deletes all film data from the MongoDB collection 'films'."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Database cleared successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> clearDatabase() {
        return databaseAdminService.clearDatabase();
    }
}