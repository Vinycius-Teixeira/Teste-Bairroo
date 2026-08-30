package com.example.repository

import com.example.models.AdminSettings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdminSettingsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val docRef = db.collection("settings").document("admin")

    suspend fun getSettings(): AdminSettings {
        val snapshot = docRef.get().await()
        return snapshot.toObject(AdminSettings::class.java) ?: AdminSettings()
    }

    suspend fun updateSettings(settings: AdminSettings) {
        docRef.set(settings).await()
    }
}
