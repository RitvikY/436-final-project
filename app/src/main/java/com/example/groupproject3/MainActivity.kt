package com.example.groupproject3


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
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var ad : InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var adUnitId : String = "ca-app-pub-3940256099942544/1033173712"
        var adRequest : AdRequest = (AdRequest.Builder( )).build( )
        var adLoad : AdLoad = AdLoad( )
        InterstitialAd.load( this, adUnitId, adRequest, adLoad )


        val buttonLogMeal = findViewById<Button>(R.id.buttonLogMeal)
        val buttonViewMeals = findViewById<Button>(R.id.buttonViewMeals)



        buttonLogMeal.setOnClickListener {

            // Intent to start LogMealActivity
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


    inner class AdLoad : InterstitialAdLoadCallback( ) {
        override fun onAdFailedToLoad(p0: LoadAdError) {
            super.onAdFailedToLoad(p0)
            Log.w( "MainActivity", "ad failed to load" )
        }

        override fun onAdLoaded(p0: InterstitialAd) {
            super.onAdLoaded(p0)
            Log.w( "MainActivity", "ad loaded" )
            // show the ad
            ad = p0
            ad!!.show( this@MainActivity )

            // handle user interaction with the ad
            var manageAd : ManageAdShowing = ManageAdShowing()
            ad!!.fullScreenContentCallback = manageAd
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

