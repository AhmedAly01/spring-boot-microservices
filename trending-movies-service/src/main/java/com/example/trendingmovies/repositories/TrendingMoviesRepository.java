package com.example.trendingmovies.repositories;

import com.example.trendingmovies.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TrendingMoviesRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT r.movieId, AVG(r.rating) FROM Rating r GROUP BY r.movieId ORDER BY AVG(r.rating) DESC")
    List<Object[]> findTop10MoviesByAverageRating(Pageable pageable);
}
