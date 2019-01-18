package io.magikwytch.movienights.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.magikwytch.movienights.entity.Movie;

import java.util.List;

public class MovieList {

    @JsonProperty("Search")
    private List<Movie> movieList;

    public MovieList() {

    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }


}
