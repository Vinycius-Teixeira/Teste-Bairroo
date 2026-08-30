package com.example.models

import com.google.firebase.Timestamp

data class LogEntry(
    val timestamp: Timestamp = Timestamp.now(),
    val type: String = "", // "error", "crash", "payment", "order", "notification"
    val message: String = "",
    val userId: String? = null,
    val details: Map<String, Any>? = null
)
