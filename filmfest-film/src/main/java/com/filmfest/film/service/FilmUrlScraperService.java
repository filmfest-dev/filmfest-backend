package com.filmfest.film.service;

import reactor.core.publisher.Mono;

public interface FilmUrlScraperService {
    Mono<Void> scrapeAndSaveFilmUrls(int year);
}