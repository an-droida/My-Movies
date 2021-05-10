package com.android.movies.ui.dashboard.view_model

import com.android.movies.utils.Constants
import com.android.movies.network.retrofit.MoviesApi


class MovieRepository(private val moviesApi:MoviesApi) {

    suspend fun getPopularMovies(page:String) = moviesApi.getPopularMovies(Constants.apiKey,page)

    suspend fun getMovies(page:String) = moviesApi.getMovies(Constants.apiKey,page)

    suspend fun getTopRatedMovies(page:String) = moviesApi.getTopRatedMovies(Constants.apiKey,page)

}