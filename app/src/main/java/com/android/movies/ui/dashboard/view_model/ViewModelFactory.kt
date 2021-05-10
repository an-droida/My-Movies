package com.android.movies.ui.dashboard.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.movies.network.retrofit.MoviesApi

class ViewModelFactory(private val movieApi: MoviesApi) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
            return MoviesViewModel(MovieRepository(movieApi)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}