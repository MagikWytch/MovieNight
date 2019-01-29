package io.magikwytch.movienights.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.magikwytch.movienights.domain.entity.Movie;

import java.util.List;

public class MovieList {

    @JsonProperty("Search")
    private List<Movie> movies;

    public MovieList() {

    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }


}
