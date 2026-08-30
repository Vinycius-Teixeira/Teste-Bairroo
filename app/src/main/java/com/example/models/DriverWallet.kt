package com.example.models

data class DriverWallet(
    val driverId: String = "",
    val balance: Double = 0.0,
    val totalRecharge: Double = 0.0,
    val totalSpent: Double = 0.0,
    val lastRecharge: Long = 0L,
    val status: String = "Active" // Active, Offline, Blocked
)
