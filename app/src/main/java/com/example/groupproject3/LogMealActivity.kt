package com.example.groupproject3

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.widget.Toast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LogMealActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 101
    private lateinit var checkBoxLogLocation: CheckBox
    private var pendingMealName: String? = null
    private var pendingMealType: String? = null
    private var pendingMealDate: String? = null
    private var pendingMealTime: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_meal)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val editTextMealName = findViewById<EditText>(R.id.editTextMealName)
        val spinnerMealType = findViewById<Spinner>(R.id.spinnerMealType)
        val timePickerMealTime = findViewById<TimePicker>(R.id.timePickerMealTime)
        val datePickerMealDate = findViewById<DatePicker>(R.id.datePickerMealDate)
        val buttonSaveMeal = findViewById<Button>(R.id.buttonSaveMeal)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        checkBoxLogLocation = findViewById(R.id.checkBoxLogLocation)

        buttonSaveMeal.setOnClickListener {
            val mealName = editTextMealName.text.toString()
            val mealType = spinnerMealType.selectedItem.toString()
            val mealTime = "${timePickerMealTime.hour}:${timePickerMealTime.minute}"
            val mealDate = "${datePickerMealDate.month + 1}-${datePickerMealDate.dayOfMonth}-${datePickerMealDate.year}"

            if (mealName.trim().isEmpty()) {
                Toast.makeText(this, "Please enter a meal name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (checkBoxLogLocation.isChecked) {
                requestLocationPermission(mealName, mealType, mealDate, mealTime)
            } else {
                saveMealWithoutLocation(mealName, mealType, mealDate, mealTime)
            }
        }

        buttonBack.setOnClickListener {
            finishActivityWithTransition()
        }
    }

    private fun requestLocationPermission(mealName: String, mealType: String, mealDate: String, mealTime: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getCurrentLocationAndSaveMeal(mealName, mealType, mealDate, mealTime)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocationAndSaveMeal(pendingMealName!!, pendingMealType!!, pendingMealDate!!, pendingMealTime!!)
            } else {
                saveMealWithoutLocation(pendingMealName!!, pendingMealType!!, pendingMealDate!!, pendingMealTime!!)
            }
        }
    }

    private fun getCurrentLocationAndSaveMeal(mealName: String, mealType: String, mealDate: String, mealTime: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 10000
                fastestInterval = 5000
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (locationResult.locations.isNotEmpty()) {
                        val location = locationResult.lastLocation
                        val newMeal = Meal(mealName, mealType, mealDate, mealTime, location!!.latitude, location!!.longitude)
                        saveMeal(newMeal)
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            saveMealWithoutLocation(mealName, mealType, mealDate, mealTime)
        }
    }



    private fun saveMealWithoutLocation(mealName: String, mealType: String, mealDate: String, mealTime: String) {
        val meal = Meal(mealName, mealType, mealDate, mealTime, null, null)
        saveMeal(meal)
        Toast.makeText(this, "Meal saved without location data", Toast.LENGTH_SHORT).show()
    }

    private fun saveMeal(meal: Meal) {
        val meals = getMeals()
        meals.add(meal)
        val sharedPreferences = getSharedPreferences("MealTrackerPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val mealsJson = gson.toJson(meals)
        editor.putString("meals", mealsJson)
        editor.apply()
        Toast.makeText(this, "Meal Saved Successfully", Toast.LENGTH_SHORT).show()
        finishActivityWithTransition()
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

    private fun finishActivityWithTransition() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        finishActivityWithTransition()
    }
}
