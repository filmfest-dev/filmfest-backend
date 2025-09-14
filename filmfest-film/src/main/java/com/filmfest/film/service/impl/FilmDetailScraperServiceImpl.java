package com.filmfest.film.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmfest.film.exception.custom.CustomBadRequestException;
import com.filmfest.film.model.FilmDetail;
import com.filmfest.film.repository.FilmDetailRepository;
import com.filmfest.film.repository.FilmUrlRepository;
import com.filmfest.film.service.FilmDetailScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmDetailScraperServiceImpl implements FilmDetailScraperService {

    private final FilmUrlRepository filmUrlRepository;
    private final FilmDetailRepository filmDetailRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder()
            .codecs(config -> config.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
            .build();

    @Override
    public Mono<Void> scrapeAndSaveFilmDetails() {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger notFoundCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        return getLanguageUrls()
                .flatMap(this::processUrl)
                .doOnNext(detail -> {
                    if (detail != null) successCount.incrementAndGet();
                    else notFoundCount.incrementAndGet();
                })
                .flatMap(detail -> detail != null ? filmDetailRepository.save(detail) : Mono.empty())
                .onErrorContinue((ex, obj) -> errorCount.incrementAndGet())
                .then()
                .doFinally(signal -> {
                    log.info("Success: {}", successCount.get());
                    log.warn("Not Found (no Movie): {}", notFoundCount.get());
                    log.error("Errors: {}", errorCount.get());
                });
    }

    private Flux<LanguageUrl> getLanguageUrls() {
        return filmUrlRepository.findAll()
                .flatMap(url -> Flux.just(
                        new LanguageUrl("ca", url.getFullUrlCa()),
                        new LanguageUrl("es", url.getFullUrlEs()),
                        new LanguageUrl("en", url.getFullUrlEn())
                ));
    }

    private Mono<FilmDetail> processUrl(LanguageUrl langUrl) {
        return Mono.fromCallable(() -> {
            Document doc = Jsoup.connect(langUrl.url()).get();
            Element jsonLdElement = doc.selectFirst("script[type=application/ld+json]");
            if (jsonLdElement == null) return null;

            JsonNode root = objectMapper.readTree(jsonLdElement.html());
            JsonNode graph = root.path("@graph");
            if (!graph.isArray()) return null;

            for (JsonNode node : graph) {
                if ("Movie".equals(node.path("@type").asText())) {
                    return buildFilmDetail(node, langUrl.language(), doc, langUrl.url());
                }
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private FilmDetail buildFilmDetail(JsonNode node, String language, Document doc, String url) {
        String title = node.path("name").asText().replace(" |", "").trim();
        String synopsis = node.path("description").asText();
        String duration = parseDuration(node.path("duration").asText());
        String posterUrl = node.path("image").path("url").asText();
        String director = extractDirector(node, doc);
        String yearText = extractYear(doc);

        List<String> countries = extractCountries(doc);

        return FilmDetail.builder()
                .id(url)
                .language(language)
                .title(title)
                .synopsis(synopsis)
                .duration(duration)
                .posterUrl(posterUrl)
                .director(director)
                .year(parseYear(yearText))
                .country(countries)
                .url(url)
                .build();
    }

    private String parseDuration(String rawDuration) {
        if (rawDuration == null || !rawDuration.startsWith("PT")) return "";
        String minutes = rawDuration.replace("PT", "").replace("S", "");
        return minutes + " min";
    }

    private int parseYear(String yearText) {
        try {
            return Integer.parseInt(yearText.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String extractDirector(JsonNode node, Document doc) {
        String director = node.path("director").path("name").asText();
        if (director != null && !director.isBlank()) {
            return director;
        }

        Element dirBlock = doc.selectFirst(".field--name-field-multi-credits");
        if (dirBlock != null && dirBlock.text().toLowerCase().contains("dirección")) {
            return Arrays.stream(dirBlock.html().split("<br>"))
                    .skip(1)
                    .map(raw -> raw.replaceAll("<[^>]*>", "").trim())
                    .filter(s -> !s.isBlank())
                    .findFirst()
                    .orElse("");
        }

        return "";
    }

    private static String extractYear(Document doc) {
        return doc.selectFirst(".field--name-field-production-year .field__item") != null
                ? doc.selectFirst(".field--name-field-production-year .field__item").text()
                : "";
    }

    private static List<String> extractCountries(Document doc) {
        return doc.select(".field--name-field-ref-film-countries .field__item").stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }

    private record LanguageUrl(String language, String url) {
    }

    private Mono<FilmDetail> scrapeFilmDetails(String lang, String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(html -> parseHtmlToFilmDetail(lang, html, url))
                .retryWhen(Retry.backoff(10, Duration.ofSeconds(10))
                        .filter(this::isRetryable)
                        .onRetryExhaustedThrow((retryBackoffSpec, signal) -> {
                            log.error("Retries exhausted for URL: {}", url, signal.failure());
                            return new CustomBadRequestException("Failed after retries: " + url);
                        })
                );
    }

    private boolean isRetryable(Throwable throwable) {
        return throwable instanceof java.io.IOException ||
                throwable instanceof org.springframework.web.reactive.function.client.WebClientRequestException;
    }

    private FilmDetail parseHtmlToFilmDetail(String lang, String html, String url) {
        return null;
    }
}