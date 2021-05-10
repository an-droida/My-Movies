package com.android.movies.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.movies.models.MovieModel

@Dao
interface MovieDao {

    @Insert
    fun addFavorite(vararg movieModel: MovieModel)

    @Query("DELETE FROM moviemodel WHERE id=:movieId")
    fun deleteFavorite(movieId: Int)

    @Query("SELECT * FROM moviemodel")
    fun getAllFavorites():List<MovieModel>

    @Query("SELECT EXISTS (SELECT 1 FROM moviemodel WHERE id = :id)")
    fun checkFavorite(id: Int): Boolean
}
