package com.android.movies.ui.details.view_model

import androidx.lifecycle.ViewModel
import com.android.movies.database.Database
import com.android.movies.models.MovieModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MovieDetailsViewModel : ViewModel() {

    fun addFavorite(movie: MovieModel) = CoroutineScope(Dispatchers.IO).launch {
        Database.db.userDao().addFavorite(movie)
    }

     fun deleteFavorite(movieId: Int) = CoroutineScope(Dispatchers.IO).launch {
        Database.db.userDao().deleteFavorite(movieId)
    }

}