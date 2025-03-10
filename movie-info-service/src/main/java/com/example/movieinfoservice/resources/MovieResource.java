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
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Map;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    private RestTemplate restTemplate;
    private final MovieRepository movieRepository;
    private final JdbcTemplate jdbcTemplate;

    public MovieResource(RestTemplate restTemplate, MovieRepository movieRepository, JdbcTemplate jdbcTemplate) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId) {
        Optional<Movie> cachedMovie = movieRepository.findByMovieId(movieId);
        if (cachedMovie.isPresent()) {
            System.out.println("Cache Hit!");
            return cachedMovie.get();
        }

        System.out.println("Cache Miss! Fetching from API...");

        Movie newMovie = getMovieFromDatabase(movieId);
        movieRepository.save(newMovie);
        System.out.println("Movie added to cache");

        return newMovie;
    }

    public Movie getMovieFromDatabase(String movieId) {
        String sql = "SELECT id, name, description FROM movies WHERE id = ?";

        // Query the database
        Map<String, Object> movieData = jdbcTemplate.queryForMap(sql, movieId);

        // Check if data exists
        if (movieData == null || movieData.isEmpty()) {
            return null;
        }

        // Create and return a new Movie object
        return new Movie(
                movieData.get("id").toString(),
                movieData.get("name").toString(),
                movieData.get("description").toString()
        );
    }
}
