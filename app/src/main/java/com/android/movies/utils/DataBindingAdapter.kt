package com.android.movies.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.android.movies.R
import com.android.movies.utils.Constants
import com.bumptech.glide.Glide

object DataBindingAdapter {
    @JvmStatic
    @BindingAdapter("setImage")
    fun loadImage(view: ImageView, image: String) {
        val imageUrl = Constants.imageUrl + image
        Glide.with(view).load(imageUrl).into(view)
    }

    @JvmStatic
    @BindingAdapter("setFavoriteIcon")
    fun setFavoriteIcon(view: ImageView, isFavorite: Boolean) {
        if (isFavorite) {
            view.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            view.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }
}