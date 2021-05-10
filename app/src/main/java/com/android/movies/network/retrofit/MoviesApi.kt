package com.android.movies.network.retrofit

import com.android.movies.models.MovieModel
import com.android.movies.network.response.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface MoviesApi {

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("api_key") key: String,@Query("page") page: String): Response<ApiResponse<List<MovieModel>>>

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") key: String,@Query("page") page: String): Response<ApiResponse<List<MovieModel>>>

    @GET("discover/movie")
    suspend fun getMovies(@Query("api_key") key: String,@Query("page") page: String): Response<ApiResponse<List<MovieModel>>>

}