package com.example.models

data class AdminSettings(
    val minRechargeAmount: Double = 10.0,
    val feePerDelivery: Double = 2.0,
    val minBalanceToAcceptOrders: Double = 5.0,
    val isCreditSystemActive: Boolean = true
)
