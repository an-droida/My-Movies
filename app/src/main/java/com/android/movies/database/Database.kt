package com.android.movies.database

import androidx.room.Room
import com.android.movies.app.App

object Database {
     val db by lazy {
        Room.databaseBuilder(
            App.instance!!.applicationContext,
            AppDatabase::class.java, "movie-database"
        ).build()
    }
}