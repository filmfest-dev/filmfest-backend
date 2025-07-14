package com.filmfest.film.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDetailDto {
    private String language;
    private String title;
    private String synopsis;
    private String duration;
    private String posterUrl;
    private String director;
    private int year;
    private List<String> country;
    private String url;
}
