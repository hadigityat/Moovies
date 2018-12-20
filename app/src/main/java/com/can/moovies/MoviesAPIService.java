package com.can.moovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesAPIService {
    @GET("discover/movie")
    Call<MoviesResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("sort_by") String sortBy,
            @Query("page") int page);

    @GET("movie/{id}/trailers")
    Call<TrailersResponse> getTrailers(
          @Path("id") int id,
          @Query("api_key") String api_key);
}
