package com.android.movies.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.movies.R
import com.android.movies.database.Database
import com.android.movies.models.MovieModel
import com.android.movies.utils.Constants
import com.android.movies.utils.CustomClick
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoviesRecyclerViewAdapter(
    private val recyclerView: RecyclerView, val movies: MutableList<MovieModel>,
    val onLoadMoreListener: OnLoadMoreListener,
    val customClick: CustomClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val LOADING_ITEM = 2
        const val LIST_ITEM = 1
    }

    private var isLoading = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private val visibleThreshold = 4


    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    onLoadMoreListener.onLoadMore()
                    isLoading = true

                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LIST_ITEM -> {
                ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.movie_items_rv_layout, parent, false)
                )
            }
            else -> {
                LoadMorePostsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.load_more_loading_layout, parent, false)
                )
            }
        }
    }


    override fun getItemCount() = movies.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val movieImageView: ImageView = itemView.findViewById(R.id.movieImageView)
        private val movieTitleTextView: TextView = itemView.findViewById(R.id.movieTitleTextView)
        lateinit var model: MovieModel

        fun onBind() {
            model = movies[adapterPosition]
            movieTitleTextView.text = model.title
            val movieImage = Constants.imageUrl + model.posterPath
            Glide.with(itemView.context).load(movieImage)
                .placeholder(R.drawable.ic_baseline_local_movies_24).into(movieImageView)
            itemView.setOnClickListener {
                customClick.onClick(adapterPosition)
            }
            CoroutineScope(Dispatchers.IO).launch {
                model.isFavorite = Database.db.userDao().checkFavorite(model.id!!)
            }
        }
    }

    inner class LoadMorePostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setLoaded() {
        isLoading = false
    }
    fun notLoaded() {
        isLoading = true
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            movies[position].isLast -> {
                LOADING_ITEM
            }
            else -> {
                LIST_ITEM
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder)
            holder.onBind()
    }
}