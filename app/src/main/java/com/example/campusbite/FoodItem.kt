package com.example.campusbite

data class FoodItem(
    val id: Int,
    val name: String,
    val price: Int,
    val isAvailable: Boolean,
    val category: String, // "All", "Today's Special", "Lunch", "Snacks", "Drinks"
    val imageResId: Int
)