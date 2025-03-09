package com.example.trendingmovies.resources;

import com.example.trendingmovies.models.Rating;
import com.example.trendingmovies.models.UserRating;
import com.example.trendingmovies.repositories.TrendingMoviesRepository;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.web.bind.annotation.*;
import com.google.protobuf.Empty;
import com.example.trendingmovies.grpc.TrendingMoviesServiceGrpc;
import com.example.trendingmovies.grpc.TrendingMoviesResponse;
import com.example.trendingmovies.grpc.Movie;
import com.example.trendingmovies.grpc.Movie.Builder;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

@GRpcService
public class TrendingMoviesResource extends TrendingMoviesServiceGrpc.TrendingMoviesServiceImplBase {

    private final TrendingMoviesRepository trendingMoviesRepository;

    public TrendingMoviesResource(TrendingMoviesRepository trendingMoviesRepository) {
        this.trendingMoviesRepository = trendingMoviesRepository;
    }

    @Override
    public void getTrendingMovies(Empty request, StreamObserver<TrendingMoviesResponse> responseObserver) {
        List<Object[]> topMovies = trendingMoviesRepository.findTop10MoviesByAverageRating(PageRequest.of(0, 10));

        // Convert to gRPC response format
        List<Movie> movies = topMovies.stream()
                .map(obj -> Movie.newBuilder()
                        .setMovieId((String) obj[0])
                        .setRating((Double) obj[1])
                        .build())
                .collect(Collectors.toList());

        // Build and send the response
        TrendingMoviesResponse response = TrendingMoviesResponse.newBuilder().addAllMovies(movies).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}