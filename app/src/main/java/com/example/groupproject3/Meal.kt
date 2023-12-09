package com.example.groupproject3


import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException



data class Meal(
    val name: String?,
    val type: String?,
    var date: String?, // Variable can be modified if date and time are swapped
    var time: String?,
    val latitude: Double?,
    val longitude: Double?
) {
    init {
        // Check if date and time are swapped and correct them if necessary
        if (!date!!.contains("-") && time!!.contains("-")) {
            val temp = date
            date = time
            time = temp
        }
    }

    // Add any additional methods or logic you need for the Meal class
}
