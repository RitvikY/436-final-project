package com.example.groupproject3


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

}

