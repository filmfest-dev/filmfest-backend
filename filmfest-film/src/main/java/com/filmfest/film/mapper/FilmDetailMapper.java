package com.filmfest.film.mapper;

import com.filmfest.film.dto.FilmDetailDto;
import com.filmfest.film.model.FilmDetail;

public class FilmDetailMapper {

    private FilmDetailMapper() {
        // Prevent instantiation
    }

    public static FilmDetail toEntity(FilmDetailDto dto) {
        return FilmDetail.builder()
                .language(dto.getLanguage())
                .title(dto.getTitle())
                .synopsis(dto.getSynopsis())
                .duration(dto.getDuration())
                .posterUrl(dto.getPosterUrl())
                .director(dto.getDirector())
                .year(dto.getYear())
                .country(dto.getCountry())
                .url(dto.getUrl())
                .id(dto.getUrl())
                .build();
    }

    public static FilmDetailDto toDto(FilmDetail entity) {
        return new FilmDetailDto(
                entity.getLanguage(),
                entity.getTitle(),
                entity.getSynopsis(),
                entity.getDuration(),
                entity.getPosterUrl(),
                entity.getDirector(),
                entity.getYear(),
                entity.getCountry(),
                entity.getUrl()
        );
    }

    /*
    private String language;
    private String title;
    private String synopsis;
    private String duration;
    private String posterUrl;
    private String director;
    private int year;
    private String country;
    private String url;
     */
}
