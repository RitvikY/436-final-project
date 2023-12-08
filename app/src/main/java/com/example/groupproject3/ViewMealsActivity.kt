package com.example.groupproject3

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.app.AlertDialog
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ViewMealsActivity : AppCompatActivity() {
    private lateinit var linearLayoutRecentMeals: LinearLayout
    private lateinit var calendarView: CalendarView
    private var meals: ArrayList<Meal> = arrayListOf()
    private var selectedDate: String = ""
    private var currentMealsDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_meals)

        meals = getMeals()
        linearLayoutRecentMeals = findViewById(R.id.linearLayoutRecentMeals)
        displayRecentMeals()

        calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = formatDate(year, month, dayOfMonth)
            showMealsForDate(selectedDate)
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() // Closes the current activity and returns to the previous one
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun displayRecentMeals() {
        // Clear the layout first to avoid duplicating views
        linearLayoutRecentMeals.removeAllViews()
        // Assuming you want to show the last 5 meals
        val recentMeals = meals.takeLast(5).reversed()
        recentMeals.forEach { meal ->
            val mealInfo = "${meal.date}: ${meal.type} - ${meal.name}"
            val mealTextView = TextView(this).apply {
                text = mealInfo
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }
            linearLayoutRecentMeals.addView(mealTextView)
        }
        Log.d("ViewMealsActivity", "Displaying recent meals: $recentMeals")
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val sdf = SimpleDateFormat("M-d-yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun showMealsForDate(date: String) {
        currentMealsDialog?.dismiss()
        val dialogView = layoutInflater.inflate(R.layout.dialog_meals_list, null)
        val linearLayoutDialogMeals = dialogView.findViewById<LinearLayout>(R.id.linearLayoutDialogMeals)

        val mealsForDate = meals.filter { it.date == date }

        mealsForDate.forEach { meal ->
            val mealTextView = TextView(this).apply {
                text = "${meal.type}: ${meal.name}"
                textSize = 16f
                setPadding(8, 8, 8, 8)
                setOnClickListener {
                    confirmAndRemoveMeal(meal, date)
                }
            }
            linearLayoutDialogMeals.addView(mealTextView)
        }

        currentMealsDialog = AlertDialog.Builder(this)
            .setTitle("Meals for $date")
            .setView(dialogView)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun confirmAndRemoveMeal(meal: Meal, date: String) {
        AlertDialog.Builder(this)
            .setTitle("Remove Meal")
            .setMessage("Do you want to remove '${meal.name}'?")
            .setPositiveButton("Remove") { _, _ ->
                removeMeal(meal, date)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun removeMeal(meal: Meal, date: String) {
        meals.remove(meal)
        saveMealsToSharedPreferences()
        currentMealsDialog?.dismiss()
        showMealsForDate(date) // Refresh the meal list for the selected date
    }

    private fun saveMealsToSharedPreferences() {
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




    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        ) // Use left-in, right-out animation
    }




}
