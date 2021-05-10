package com.android.movies.ui.details

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.movies.R
import com.android.movies.databinding.ActivityMovieDetailsBinding
import com.android.movies.models.MovieModel
import com.android.movies.utils.Constants.ADAPTER_POSITION
import com.android.movies.ui.dashboard.MoviesActivity.Companion.movieItems
import com.android.movies.ui.details.view_model.MovieDetailsVieModelFactory
import com.android.movies.ui.details.view_model.MovieDetailsViewModel

class MovieDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityMovieDetailsBinding
    private var adapterPosition: Int? = null
    lateinit var movieDetailsViewModel: MovieDetailsViewModel
    lateinit var detailedMovie : MovieModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details)

        movieDetailsViewModel =
            ViewModelProvider(this, MovieDetailsVieModelFactory()).get(
                MovieDetailsViewModel::class.java
            )

        adapterPosition = intent.extras?.getInt(ADAPTER_POSITION)
        detailedMovie = movieItems[adapterPosition!!]

        binding.detailsViewModel = movieDetailsViewModel
        binding.movieModel = detailedMovie
        binding.lifecycleOwner = this

        initToolbar()

        binding.favoriteImageView.setOnClickListener {
            if (detailedMovie.isFavorite) {
                movieItems[adapterPosition!!].isFavorite = false
                binding.favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                movieDetailsViewModel.deleteFavorite(detailedMovie.id!!)
            } else {
                movieItems[adapterPosition!!].isFavorite = true
                binding.favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24)
                movieDetailsViewModel.addFavorite(detailedMovie)
            }
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.detailsToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.detailsToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

}