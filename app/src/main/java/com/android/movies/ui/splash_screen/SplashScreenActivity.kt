package com.android.movies.ui.splash_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import com.android.movies.R
import com.android.movies.databinding.ActivitySplashScreenBinding
import com.android.movies.ui.dashboard.MoviesActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        binding.logoSplashImage.animation = splashAnimation

    }

    override fun onStart() {
        super.onStart()
        handler.postDelayed(runnable, 3000)
    }


    private fun init() {
        handler = Handler(this.mainLooper)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private val runnable = Runnable {
        val intent = Intent(this, MoviesActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}