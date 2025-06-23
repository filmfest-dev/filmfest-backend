package com.filmfest.film.repository;

import com.filmfest.film.model.FilmDetail;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FilmDetailRepository extends ReactiveMongoRepository<FilmDetail, String> {
}
