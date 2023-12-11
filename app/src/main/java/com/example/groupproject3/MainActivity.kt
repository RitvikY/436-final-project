package com.example.groupproject3


/* Group Project: Ritvik Yaragudipati, Roshini Parameswaran, Abhinav Yedla */

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var ad : InterstitialAd? = null

    lateinit var mAdView : AdView
    lateinit var adView : AdView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        val buttonLogMeal = findViewById<Button>(R.id.buttonLogMeal)
        val buttonViewMeals = findViewById<Button>(R.id.buttonViewMeals)



        buttonLogMeal.setOnClickListener {

            val intent = Intent(this, LogMealActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }


        buttonViewMeals.setOnClickListener {
            val intent = Intent(this, ViewMealsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)




        // Initialize the AdView
        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()

        // Set the AdListener
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("MainActivity", "Ad failed to load: ${loadAdError.message}")
                adView.loadAd(AdRequest.Builder().build()) // Request a new ad
            }

        }


        adView.loadAd(adRequest)

    }

    private fun loadBannerAds(){
        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        mAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                Log.w("MainActivity", "Ad closed, retturning")
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                Log.w("MainActivity", "Ad did not load, retturning")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                Log.w("MainActivity", "Ad Loaded")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }

    }




    private fun displayLocations(textView: TextView) {
        val meals = getMeals() // Method to fetch saved meals
        val locationText = buildLocationText(meals)
        textView.text = locationText
    }

    private fun buildLocationText(meals: ArrayList<Meal>): String {
        return meals.joinToString("\n") { meal ->
            if (meal.latitude != null && meal.longitude != null) {
                "${meal.name}: Lat ${meal.latitude}, Long ${meal.longitude}"
            } else {
                "${meal.name}: Location not available"
            }
        }
    }

    private fun getMeals(): ArrayList<Meal> {
        val sharedPreferences = getSharedPreferences("MealTrackerPrefs", MODE_PRIVATE)
        val gson = Gson()
        val mealsJson = sharedPreferences.getString("meals", null)
        return if (mealsJson != null) {
            val type = object : TypeToken<ArrayList<Meal>>() {}.type
            gson.fromJson(mealsJson, type)
        } else {
            ArrayList()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add markers for each saved location
        val meals = getMeals()
        for (meal in meals) {
            if (meal.latitude != null && meal.longitude != null) {
                val location = LatLng(meal.latitude, meal.longitude)
                mMap.addMarker(MarkerOptions().position(location).title(meal.name))
            }
        }
    }



    inner class ManageAdShowing : FullScreenContentCallback( ) {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            Log.w( "MainActivity", "user closed the ad" )
            // some code here to continue the app
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.w( "MainActivity", "User clicked on the ad" )
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.w( "MainActivity", "user has seen the ad" )
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            Log.w( "MainActivity", "ad has been shown" )
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            Log.w( "MainActivity", "ad failed to show" )
        }
    }





}

