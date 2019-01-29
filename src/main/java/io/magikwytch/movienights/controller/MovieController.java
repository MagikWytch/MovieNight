package io.magikwytch.movienights.controller;

import io.magikwytch.movienights.domain.entity.Movie;
import io.magikwytch.movienights.domain.MovieList;

import io.magikwytch.movienights.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;
    private RestTemplate restTemplate = new RestTemplate();
    private String omdbURL = "https://www.omdbapi.com/?apikey=972c6b98";

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Movie> getMovieById(@PathVariable("id") String id) {

        if (movieRepository.findByImdbId(id) == null) {
            Movie movie = restTemplate.getForObject(omdbURL + "&i=" + id, Movie.class);
            if (movie == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            movieRepository.save(movie);
        }

        return new ResponseEntity<>(movieRepository.findByImdbId(id), HttpStatus.OK);

    }

    @RequestMapping(value = "/search/{key}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<Movie>> getMoviesByTitle(@PathVariable("key") String key) {

        MovieList response = restTemplate.getForObject(omdbURL + "&s=" + key, MovieList.class);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Movie> movies = response.getMovies();

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}