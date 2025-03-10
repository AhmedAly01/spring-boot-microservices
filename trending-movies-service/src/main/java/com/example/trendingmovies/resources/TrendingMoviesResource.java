package com.example.trendingmovies.resources;

import com.example.trendingmovies.grpc.*;
import com.example.trendingmovies.repositories.TrendingMoviesRepository;
import com.example.trendingmovies.models.MovieInfo;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@GRpcService
public class TrendingMoviesResource extends TrendingMoviesServiceGrpc.TrendingMoviesServiceImplBase {

    private final TrendingMoviesRepository trendingMoviesRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String MOVIE_DETAILS_URL = "http://localhost:8082/movies/";

    public TrendingMoviesResource(TrendingMoviesRepository trendingMoviesRepository) {
        this.trendingMoviesRepository = trendingMoviesRepository;
    }

    @Override
    public void getTrendingMovies(Empty request, StreamObserver<TrendingMoviesResponse> responseObserver) {
        List<Object[]> topMovies = trendingMoviesRepository.findTop10MoviesByAverageRating(PageRequest.of(0, 10));

        List<Movie> movies = topMovies.stream().map(obj -> {
            String movieId = (String) obj[0];
            double avgRating = (double) obj[1];

            MovieInfo details = fetchMovieDetails(movieId);

            return Movie.newBuilder()
                    .setMovieId(movieId)
                    .setRating(avgRating)
                    .setName(details.getName())
                    .setDescription(details.getDescription())
                    .build();
        }).collect(Collectors.toList());

        TrendingMoviesResponse response = TrendingMoviesResponse.newBuilder().addAllMovies(movies).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private MovieInfo fetchMovieDetails(String movieId) {
        try {
            return restTemplate.getForObject(MOVIE_DETAILS_URL + movieId, MovieInfo.class);
        } catch (Exception e) {
            return new MovieInfo();
        }
    }
}
