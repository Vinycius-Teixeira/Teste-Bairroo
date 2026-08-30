package com.example.repository

import com.example.models.LogEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LogRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun logEvent(logEntry: LogEntry) {
        try {
            db.collection("logs").add(logEntry).await()
        } catch (e: Exception) {
            // Log locally or handle silent failure as this is a logging service
        }
    }
}
