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
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        buttonSaveMeal.setOnClickListener {
            val mealName = editTextMealName.text.toString()
            val mealType = spinnerMealType.selectedItem.toString()
            val mealTime = "${timePickerMealTime.hour}:${timePickerMealTime.minute}"
            val mealDate = "${datePickerMealDate.month + 1}-${datePickerMealDate.dayOfMonth}-${datePickerMealDate.year}"

            if (mealName.trim().isEmpty()) {
                Toast.makeText(this, "Please enter a meal name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newMeal = Meal(mealName, mealType, mealDate, mealTime)
            val meals = getMeals()
            meals.add(newMeal)
            saveMeals(meals)

            Toast.makeText(this, "Meal Saved Successfully", Toast.LENGTH_SHORT).show()

            finishActivityWithTransition()
        }

        buttonBack.setOnClickListener {
            finishActivityWithTransition()
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

    private fun finishActivityWithTransition() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        finishActivityWithTransition()
    }
}
