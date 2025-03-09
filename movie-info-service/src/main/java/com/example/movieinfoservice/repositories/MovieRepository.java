package com.example.movieinfoservice.repositories;

import com.example.movieinfoservice.models.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {
    Optional<Movie> findByMovieId(String movieId);
}
