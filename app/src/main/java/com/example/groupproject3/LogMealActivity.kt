package com.example.groupproject3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.widget.Toast


class LogMealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_meal)

        val editTextMealName = findViewById<EditText>(R.id.editTextMealName)
        val spinnerMealType = findViewById<Spinner>(R.id.spinnerMealType)
        val timePickerMealTime = findViewById<TimePicker>(R.id.timePickerMealTime)
        val datePickerMealDate = findViewById<DatePicker>(R.id.datePickerMealDate)
        val buttonSaveMeal = findViewById<Button>(R.id.buttonSaveMeal)

        buttonSaveMeal.setOnClickListener {
            val mealName = editTextMealName.text.toString()
            val mealType = spinnerMealType.selectedItem.toString()
            val mealTime = "${timePickerMealTime.hour}:${timePickerMealTime.minute}"

            // Adjusting the month value as DatePicker returns month indexed from 0
            val mealDate = "${datePickerMealDate.dayOfMonth}-${datePickerMealDate.month + 1}-${datePickerMealDate.year}"

            val newMeal = Meal(mealName, mealType, mealDate, mealTime)
            val meals = getMeals()
            meals.add(newMeal)
            saveMeals(meals)

            // Show confirmation message
            Toast.makeText(this, "Meal Saved Successfully", Toast.LENGTH_SHORT).show()

            // Optionally, navigate back to the Home Screen
            finish()
        }
    }

    private fun saveMeals(meals: ArrayList<Meal>) {
        val sharedPreferences = getSharedPreferences("MealTrackerPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val mealsJson = gson.toJson(meals)
        editor.putString("meals", mealsJson)
        editor.apply()
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
}
