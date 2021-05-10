package com.android.movies.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.movies.models.MovieModel

@Database(entities = [MovieModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): MovieDao

}