package com.example.movieinfoservice.resources;

import com.example.movieinfoservice.models.Movie;
import com.example.movieinfoservice.models.MovieSummary;
import com.example.movieinfoservice.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    private RestTemplate restTemplate;
    private final MovieRepository movieRepository;

    public MovieResource(RestTemplate restTemplate, MovieRepository movieRepository) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
    }

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId) {
        Optional<Movie> cachedMovie = movieRepository.findByMovieId(movieId);
        if (cachedMovie.isPresent()) {
            System.out.println("Cache Hit!");
            return cachedMovie.get();
        }

        System.out.println("Cache Miss! Fetching from API...");

        // Get the movie info from TMDB
        final String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        MovieSummary movieSummary = restTemplate.getForObject(url, MovieSummary.class);

        if (movieSummary == null) {
            return null;
        }

        Movie newMovie = new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());
        movieRepository.save(newMovie);
        System.out.println("Movie added to cache");

        return newMovie;
    }
}
