package com.filmfest.film.mapper;

import com.filmfest.film.dto.FilmUrlDto;
import com.filmfest.film.model.FilmUrl;

import java.util.Map;

public class FilmUrlMapper {

    private FilmUrlMapper() {
    }

    @SuppressWarnings("unchecked")
    public static FilmUrlDto fromMap(Map<String, Object> filmData) {
        Map<String, String> urlMap = (Map<String, String>) filmData.get("url");

        return new FilmUrlDto(
                urlMap.get("ca"),
                urlMap.get("en"),
                urlMap.get("es")
        );
    }

    public static FilmUrl toEntity(FilmUrlDto dto) {
        return FilmUrl.builder()
                .fullUrlCa(dto.getUrlCa())
                .fullUrlEn(dto.getUrlEn())
                .fullUrlEs(dto.getUrlEs())
                .build();
    }

    public static FilmUrlDto toDto(FilmUrl entity) {
        return new FilmUrlDto(
                entity.getFullUrlCa(),
                entity.getFullUrlEn(),
                entity.getFullUrlEs()
        );
    }
}
