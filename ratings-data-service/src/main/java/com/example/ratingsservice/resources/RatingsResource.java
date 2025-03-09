package com.example.ratingsservice.resources;

import com.example.ratingsservice.models.Rating;
import com.example.ratingsservice.models.UserRating;
import com.example.ratingsservice.repositories.RatingRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ratings")
public class RatingsResource {

    private final RatingRepository ratingRepository;

    public RatingsResource(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @RequestMapping("/{userId}")
    public List<Rating> getRatingsOfUser(@PathVariable Long userId) {
        return ratingRepository.findByUserId(userId)
                .stream()
                .map(rating -> new Rating(rating.getMovieId(), rating.getRating()))
                .collect(Collectors.toList());
    }
}
