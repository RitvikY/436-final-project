package com.example.groupproject3


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog


class MainActivity : AppCompatActivity() {

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

    }
}

