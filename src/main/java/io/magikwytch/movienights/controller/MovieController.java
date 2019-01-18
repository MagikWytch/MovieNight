package io.magikwytch.movienights.controller;

import io.magikwytch.movienights.entity.Movie;
import io.magikwytch.movienights.entity.MovieList;
import io.magikwytch.movienights.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    private String key = "972c6b98";
    private RestTemplate restTemplate = new RestTemplate();

    /*@RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public @ResponseBody Movie getMovieById(@PathVariable("id") String id) {
        return restTemplate.getForObject("https://www.omdbapi.com/?apikey=" + key + "&i=" + id, Movie.class);
    }*/

    @RequestMapping(value = "/title/{title}", method = RequestMethod.GET)
    public Movie getMovieByTitle(@PathVariable("title") String title) {

        if (movieRepository.findDistinctFirstByTitleIgnoreCase(title) == null) {
            Movie movie = restTemplate.getForObject("https://www.omdbapi.com/?apikey=" + key + "&t=" + title, Movie.class);
            movieRepository.save(movie);
            return movie;
        }

        return movieRepository.findDistinctFirstByTitleIgnoreCase(title);
    }

    @RequestMapping(value = "/search/{keyword}", method = RequestMethod.GET)
    public @ResponseBody
    MovieList searchMovieByKeyword(@PathVariable("keyword") String keyword) {
        return restTemplate.getForObject("https://www.omdbapi.com/?apikey=" + key + "&s=" + keyword, MovieList.class);
    }


}