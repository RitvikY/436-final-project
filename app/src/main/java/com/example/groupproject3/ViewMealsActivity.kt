package com.example.groupproject3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.app.AlertDialog
import android.widget.Toast

class ViewMealsActivity : AppCompatActivity() {
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var meals: ArrayList<Meal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_meals)

        meals = getMeals()
        val listViewMeals = findViewById<ListView>(R.id.listViewMeals)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, meals.map { it.name })
        listViewMeals.adapter = adapter

        listViewMeals.setOnItemClickListener { _, _, position, _ ->
            val selectedMeal = meals[position]
            showMealDetails(selectedMeal)
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() // Closes the current activity and returns to the previous one
        }
    }

    private fun showMealDetails(meal: Meal) {
        AlertDialog.Builder(this)
            .setTitle(meal.name)
            .setMessage("Type: ${meal.type}\nDate: ${meal.date}\nTime: ${meal.time}")
            .setPositiveButton("OK", null)
            .setNegativeButton("Remove") { dialog, which ->
                removeMeal(meal)
            }
            .show()
    }

    private fun removeMeal(meal: Meal) {
        meals.remove(meal)

        val sharedPreferences = getSharedPreferences("MealTrackerPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val mealsJson = gson.toJson(meals)
        editor.putString("meals", mealsJson)
        editor.apply()

        adapter.clear()
        adapter.addAll(meals.map { it.name })
        adapter.notifyDataSetChanged()

        Toast.makeText(this, "Meal removed", Toast.LENGTH_SHORT).show()
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
