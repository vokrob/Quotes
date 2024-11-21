package com.vokrob.quotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.vokrob.quotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdMob()
    }

    override fun onResume() {
        super.onResume()

        binding.adView.resume()
    }

    override fun onPause() {
        super.onPause()

        binding.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.adView.destroy()
    }

    private fun initAdMob() {
        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }
}





























