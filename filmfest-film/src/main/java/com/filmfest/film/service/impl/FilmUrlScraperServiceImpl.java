package com.filmfest.film.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmfest.film.model.FilmUrl;
import com.filmfest.film.repository.FilmUrlRepository;
import com.filmfest.film.service.FilmUrlScraperService;
import com.filmfest.film.exception.custom.CustomBadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmUrlScraperServiceImpl implements FilmUrlScraperService {

    private final FilmUrlRepository filmUrlRepository;
    private final WebClient webClient = WebClient.builder()
            .codecs(config -> config.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
            .build();

    private static final String BASE_URL = "https://sitgesfilmfestival.com";
    private static final String FILM_URL_TEMPLATE = "https://sitgesfilmfestival.com/service/films/%d";

    @Override
    public Mono<Void> scrapeAndSaveFilmUrls(int year) {
        return filmUrlRepository.deleteAll()
                .thenMany(fetchFilmDataFromRemote(year))
                .map(this::extractFilmUrl)
                .flatMap(filmUrlRepository::save)
                .then();
    }

    private Flux<Map<String, Object>> fetchFilmDataFromRemote(int year) {
        String uri = String.format(FILM_URL_TEMPLATE, year);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .flatMapMany(this::extractFilmList)
                .onErrorResume(e -> {
                    log.error("Failed to fetch or parse JSON from {}", uri, e);
                    return Flux.error(new CustomBadRequestException("Error fetching/parsing JSON"));
                });
    }

    @SuppressWarnings("unchecked")
    private Flux<Map<String, Object>> extractFilmList(Map<String, Object> jsonMap) {
        Object films = jsonMap.get("films");
        if (films instanceof List<?>) {
            return Flux.fromIterable((List<Map<String, Object>>) films);
        } else {
            log.error("Invalid JSON: expected 'films' to be a list");
            return Flux.error(new CustomBadRequestException("Invalid JSON structure: expected 'films' to be a list"));
        }
    }


    private FilmUrl extractFilmUrl(Map<String, Object> filmData) {
        Map<String, String> urlMap = (Map<String, String>) filmData.get("url");
        String slug = Optional.ofNullable(urlMap.get("ca"))
                .map(url -> url.substring(url.lastIndexOf('/') + 1))
                .orElse("unknown-" + System.currentTimeMillis());

        return buildFilmUrlFromSlugAndMap(slug, urlMap);
    }

    private FilmUrl buildFilmUrlFromSlugAndMap(String slug, Map<String, String> urlMap) {
        return FilmUrl.builder()
                .id(slug)
                .fullUrlCa(BASE_URL+ urlMap.get("ca"))
                .fullUrlEs(BASE_URL + urlMap.get("es"))
                .fullUrlEn(BASE_URL + urlMap.get("en"))
                .build();
    }
}