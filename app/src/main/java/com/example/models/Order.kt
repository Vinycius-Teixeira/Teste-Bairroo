package com.example.models

data class StoreOrder(
    val id: String = "",
    val customerName: String = "",
    val itemsSummary: String = "",
    val totalPrice: Double = 0.0,
    var status: String = "", // "Pendente", "Aceito", "Em Preparo", "Pronto", "Enviado", "Entregue"
    val time: String = "",
    val category: String = "Restaurante",
    var commissionRateApplied: Float? = null,
    val customerId: String = "",
    val restaurantId: String = "",
    val driverId: String = "",
    val timestamp: Long = 0L
)

data class CancellationRecord(
    val id: String,
    val source: String,
    val orderId: String,
    val date: String,
    val totalOrderValue: Double,
    val fineValue: Double,
    val splitLojista: Double,
    val splitBairroo: Double
)
