package com.filmfest.film.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "film_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmDetail {

    @Id
    private String id;

    private String language;
    private String title;
    private String synopsis;
    private String duration;
    private String director;
    private String posterUrl;
    private String url;
    private List<String> country;
    private int year;
}
