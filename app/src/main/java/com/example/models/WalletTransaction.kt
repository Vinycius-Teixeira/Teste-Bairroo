package com.example.models

data class WalletTransaction(
    val id: String = "",
    val driverId: String = "",
    val amount: Double = 0.0,
    val type: String = "", // "Recharge", "Deduction", "Refund"
    val timestamp: Long = 0L,
    val orderId: String? = null,
    val previousBalance: Double = 0.0,
    val newBalance: Double = 0.0,
    val reason: String = ""
)
