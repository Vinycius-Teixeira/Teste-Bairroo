package com.example.models

data class FoodItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageType: String = "pizza",
    val category: String = "Destaques"
)
