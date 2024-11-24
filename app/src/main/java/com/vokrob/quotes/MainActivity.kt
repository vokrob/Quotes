package com.vokrob.quotes

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.vokrob.quotes.adapters.CategoryAdapter
import com.vokrob.quotes.adapters.ContentManager
import com.vokrob.quotes.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity(), CategoryAdapter.Listener {
    private lateinit var binding: ActivityMainBinding
    private var adapter: CategoryAdapter? = null
    private var interAd: InterstitialAd? = null
    private var currentCategory: Int = -1
    private var isAnimating: Boolean = false
    private lateinit var inAnimation: Animation
    private lateinit var outAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAnimations()
        initAdMob()
        (application as AppMainState).showAdIfAvailable(this) {}
        initRcView()

        binding.imageBg.setOnClickListener {
            if (currentCategory != -1 && !isAnimating) {
                startNewMessageAnimation()
            }
        }
    }

    private fun initAnimations() {
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_in)
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_out)

        inAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                isAnimating = true
            }

            override fun onAnimationEnd(animation: Animation?) {
                isAnimating = false
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        outAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                isAnimating = true
            }

            override fun onAnimationEnd(animation: Animation?) {
                getMessage()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun initRcView() = with(binding) {
        adapter = CategoryAdapter(this@MainActivity)
        rcViewCat.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rcViewCat.adapter = adapter
        adapter?.submitList(ContentManager.list)
    }

    private fun startNewMessageAnimation() {
        if (!isAnimating) {
            binding.apply {
                tvMessage.startAnimation(outAnimation)
                tvName.startAnimation(outAnimation)
                imageBg.startAnimation(outAnimation)
            }
        }
    }

    private fun getMessage() = with(binding) {
        val currentArray = resources.getStringArray(MainConst.arrayList[currentCategory])
        val message = currentArray[Random.nextInt(currentArray.size)]
        val messageList = message.split("|")

        tvMessage.text = messageList[0]
        tvName.text = messageList[1]
        imageBg.setImageResource(MainConst.imageList[Random.nextInt(4)])

        tvMessage.startAnimation(inAnimation)
        tvName.startAnimation(inAnimation)
        imageBg.startAnimation(inAnimation)
    }

    override fun onClick(pos: Int) {
        if (!isAnimating) {
            if (currentCategory == -1) {
                currentCategory = pos
                binding.cardView.visibility = View.VISIBLE
                getMessage()
            } else {
                currentCategory = pos
                startNewMessageAnimation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
        loadInterAd()
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

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interAd = ad
                }
            }
        )
    }

    private fun showInterAd() {
        if (interAd != null) {
            interAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        showContent()
                        interAd = null
                        loadInterAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        showContent()
                        interAd = null
                        loadInterAd()
                    }

                    override fun onAdShowedFullScreenContent() {
                        interAd = null
                        loadInterAd()
                    }
                }
            interAd?.show(this)
        } else {
            showContent()
        }
    }

    private fun showContent() {
        Toast.makeText(this, "Запуск контента", Toast.LENGTH_LONG).show()
    }
}
























