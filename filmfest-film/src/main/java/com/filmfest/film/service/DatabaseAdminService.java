package com.filmfest.film.service;

import reactor.core.publisher.Mono;

public interface DatabaseAdminService {
    Mono<Void> clearDatabase();
}
