package com.android.movies.ui.dashboard.view_model

import android.util.Log.d
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.movies.database.Database
import com.android.movies.models.MovieModel
import com.android.movies.network.response.ApiResponse
import com.android.movies.network.response.BaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Response

class MoviesViewModel(private val movieRepository: MovieRepository) : ViewModel() {


    val allMovieResponse: MutableLiveData<BaseResponse<List<MovieModel>>> = MutableLiveData()
    val favoritesResponse: MutableLiveData<List<MovieModel>> = MutableLiveData()
    val pageCount:MutableLiveData<Int> = MutableLiveData()

    fun getTopRatedMovies(page:String) = viewModelScope.launch {
        allMovieResponse.postValue(BaseResponse.Loading())
        try {
            val response = movieRepository.getTopRatedMovies(page)
            allMovieResponse.postValue(handleMoviesResponse(response))
        } catch (exception: Exception) {
            d("exception", exception.toString())
        }
    }

    fun getPopularMovies(page:String) = viewModelScope.launch {
        allMovieResponse.postValue(BaseResponse.Loading())
        try {
            val response = movieRepository.getPopularMovies(page)
            allMovieResponse.postValue(handleMoviesResponse(response))
        } catch (exception: Exception) {
            d("exception", exception.toString())
        }
    }


    fun getAllMovies(page:String) = viewModelScope.launch {
        allMovieResponse.postValue(BaseResponse.Loading())
        try {
            val response = movieRepository.getMovies(page)
            allMovieResponse.postValue(handleMoviesResponse(response))
        } catch (exception: Exception) {
            d("exception", exception.toString())
        }
    }

    private fun handleMoviesResponse(response: Response<ApiResponse<List<MovieModel>>>):
            BaseResponse<List<MovieModel>> {
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.results!!.isNotEmpty()) {
                    pageCount.postValue(it.totalPages)
                    return BaseResponse.Success(it.results!!)
                }
            }
        }
        return BaseResponse.Failure(response.message())
    }

    fun getFavorites() = CoroutineScope(IO).launch {
        favoritesResponse.postValue(Database.db.userDao().getAllFavorites())
    }
}