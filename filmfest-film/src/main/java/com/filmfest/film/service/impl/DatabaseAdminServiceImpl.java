package com.filmfest.film.service.impl;

import com.filmfest.film.service.DatabaseAdminService;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DatabaseAdminServiceImpl implements DatabaseAdminService {

    private static final String FILM_URLS = "film_urls";
    private static final String FILM_DETAILS = "film_details";

    private final ReactiveMongoTemplate mongoTemplate;

    public DatabaseAdminServiceImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Void> clearDatabase() {
        return Mono.when(
                mongoTemplate.dropCollection(FILM_URLS),
                mongoTemplate.dropCollection(FILM_DETAILS)
        ).then();
    }
}