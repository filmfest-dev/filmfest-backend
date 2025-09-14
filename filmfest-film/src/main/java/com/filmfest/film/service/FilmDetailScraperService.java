package com.filmfest.film.service;

import reactor.core.publisher.Mono;

public interface FilmDetailScraperService {
    Mono<Void> scrapeAndSaveFilmDetails();
}
