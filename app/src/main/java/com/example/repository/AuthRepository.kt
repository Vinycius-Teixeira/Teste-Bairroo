package com.example.repository

import com.example.models.BairrooAddress
import com.example.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val db = FirebaseFirestore.getInstance()

    fun authenticate(email: String, password: String, allUsers: List<User>): User? {
        return allUsers.find { 
            it.email.equals(email, ignoreCase = true) && 
            it.password == password && 
            it.active 
        }
    }

    suspend fun saveAddress(userId: String, address: BairrooAddress) {
        db.collection("users").document(userId)
            .collection("addresses").document(address.id).set(address).await()
    }

    suspend fun getAddresses(userId: String): List<BairrooAddress> {
        val snapshot = db.collection("users").document(userId)
            .collection("addresses").get().await()
        return snapshot.toObjects(BairrooAddress::class.java)
    }

    suspend fun saveFcmToken(userId: String, token: String) {
        db.collection("users").document(userId)
            .update("fcmToken", token).await()
    }
}
