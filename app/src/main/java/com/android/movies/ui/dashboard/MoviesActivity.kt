package com.android.movies.ui.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log.d
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.android.movies.R
import com.android.movies.databinding.ActivityMoviesBinding
import com.android.movies.models.MovieModel
import com.android.movies.network.response.BaseResponse
import com.android.movies.network.retrofit.RetrofitBuilder
import com.android.movies.ui.dashboard.adapter.MoviesRecyclerViewAdapter
import com.android.movies.ui.dashboard.adapter.OnLoadMoreListener
import com.android.movies.ui.dashboard.view_model.MoviesViewModel
import com.android.movies.ui.dashboard.view_model.ViewModelFactory
import com.android.movies.ui.details.MovieDetailsActivity
import com.android.movies.utils.Constants
import com.android.movies.utils.Constants.ADAPTER_POSITION
import com.android.movies.utils.CustomClick
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.net.InetAddress


class MoviesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoviesBinding
    private lateinit var viewModel: MoviesViewModel
    private lateinit var adapter: MoviesRecyclerViewAdapter
    lateinit var snackBar: Snackbar
    var notConnected: Boolean? = null
    var countPages = 2
    var isLoadMore = false

    companion object {
        val movieItems = mutableListOf<MovieModel>()
        var movieListType = 0
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            notConnected = intent.getBooleanExtra(
                ConnectivityManager
                    .EXTRA_NO_CONNECTIVITY, false
            )
            if (notConnected!!) {
                disconnected()
            } else {
                connected()
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddress: InetAddress = InetAddress.getByName("api.themoviedb.org")
            d("Connected", ipAddress.toString())
            !ipAddress.equals("")
        } catch (e: Exception) {
            d("exception", e.toString())
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainToolbar.title = getString(R.string.my_movies)
        setupViewModel()

        setupObservers()
        initAdapter()

        viewModel.getAllMovies("1")
        setSupportActionBar(binding.mainToolbar)

        snackBar = Snackbar.make(
            binding.containerLayout,
            getString(R.string.no_connection),
            Snackbar.LENGTH_INDEFINITE
        )
    }

    private fun disconnected() {
        snackBar.show()
        if (movieListType != 3) {
            binding.moviesRecyclerView.visibility = View.INVISIBLE
        }
    }

    private fun connected() {
        binding.moviesRecyclerView.visibility = View.VISIBLE
        when (movieListType) {
            0 -> viewModel.getAllMovies("1")
            1 -> viewModel.getPopularMovies("1")
            2 -> viewModel.getTopRatedMovies("1")
        }
        snackBar.dismiss()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private fun initAdapter() {
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    MoviesRecyclerViewAdapter.LOADING_ITEM -> 2
                    MoviesRecyclerViewAdapter.LIST_ITEM -> 1
                    else -> -1
                }
            }
        }

        binding.moviesRecyclerView.layoutManager = layoutManager
        adapter = MoviesRecyclerViewAdapter(
            binding.moviesRecyclerView,
            movieItems,
            loadMoreListener,
            object : CustomClick {
                override fun onClick(position: Int) {
                    val intent = Intent(this@MoviesActivity, MovieDetailsActivity::class.java)
                    intent.putExtra(ADAPTER_POSITION, position)
                    startActivityForResult(intent, Constants.UPDATE_FAVORITE_CODE)
                }
            })
        binding.moviesRecyclerView.adapter = adapter
    }

    private val loadMoreListener = object :
        OnLoadMoreListener {
        override fun onLoadMore() {
            if (movieItems.size != 0) {
                if (!movieItems[movieItems.size - 1].isLast) {
                    binding.moviesRecyclerView.post {
                        if (countPages <= viewModel.pageCount.value!!) {
                            val movieModel = MovieModel()
                            movieModel.isLast = true
                            movieItems.add(movieModel)
                            adapter.notifyItemInserted(movieItems.size - 1)
                            Handler(this@MoviesActivity.mainLooper).postDelayed({
                                getMoreMovies(countPages.toString())
                            }, 1000)
                        }
                    }
                }
            }
        }
    }

    private fun getMoreMovies(countPages: String) {
        isLoadMore = true
        when (movieListType) {
            0 -> {
                viewModel.getAllMovies(countPages)
            }
            1 -> {
                viewModel.getPopularMovies(countPages)
            }
            2 -> {
                viewModel.getTopRatedMovies(countPages)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitBuilder.apiService)
        ).get(MoviesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.allMovieResponse.observe(this, { response ->
            when (response) {
                is BaseResponse.Success -> {
                    if (!isLoadMore) {
                        movieItems.clear()
                        response.data!!.forEach {
                            movieItems.add(it)
                        }
                    } else {
                        countPages++
                        val lastPosition = movieItems.size
                        if (lastPosition > 0) {
                            movieItems.removeAt(movieItems.size - 1)
                            adapter.notifyItemRemoved(movieItems.size - 1)
                        }
                        adapter.setLoaded()

                        response.data!!.forEach {
                            movieItems.add(it)
                        }
                        if (movieItems.isNotEmpty() && movieItems.size != 1) adapter.notifyItemMoved(
                            lastPosition,
                            movieItems.size - 1
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
                is BaseResponse.Loading -> {
                    CoroutineScope(IO).launch {
                        if (!isInternetAvailable()) {
                            snackBar.show()
                            binding.moviesRecyclerView.visibility = View.INVISIBLE
                        } else {
                            snackBar.dismiss()
                            binding.moviesRecyclerView.visibility = View.VISIBLE
                        }
                    }
                }
                is BaseResponse.Failure -> {
                    Toast.makeText(this, "Load Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.favoritesResponse.observe(this, {
            movieItems.clear()
            it.forEach {
                movieItems.add(it)
            }
            adapter.notifyDataSetChanged()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.UPDATE_FAVORITE_CODE && resultCode == RESULT_OK) {
            if (movieListType == 3) {
                viewModel.getFavorites()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.getPopular -> {
                isLoadMore = false
                viewModel.getPopularMovies("1")
                movieListType = 1
                binding.mainToolbar.title = getString(R.string.popular)
                countPages = 2
                adapter.setLoaded()
                true
            }
            R.id.getTopRated -> {
                isLoadMore = false
                viewModel.getTopRatedMovies("1")
                movieListType = 2
                binding.mainToolbar.title = getString(R.string.top_rated)
                countPages = 2
                adapter.setLoaded()
                true
            }
            R.id.getFavorites -> {
                isLoadMore = false
                binding.moviesRecyclerView.visibility = View.VISIBLE
                viewModel.getFavorites()
                movieListType = 3
                binding.mainToolbar.title = getString(R.string.my_favorites)
                countPages = 2
                adapter.notLoaded()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}