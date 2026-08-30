package com.example.models

data class Restaurant(
    val id: String,
    val name: String,
    val rating: Double,
    val category: String,
    val time: String,
    val shipping: String,
    val coupon: String = "",
    val isPartner: Boolean = true,
    val imageType: String = "pizza",
    var score: Int = 100,
    var bairrooMaisParticipation: Boolean = false,
    var bairroooMaisBudget: Double = 0.0,
    var bairroooMaisBudgetUsed: Double = 0.0
)
