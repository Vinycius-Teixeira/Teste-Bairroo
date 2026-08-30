package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.database.BairroooDatabase
import com.example.repository.CartRepository
import com.example.repository.OrderRepository
import com.google.firebase.firestore.FirebaseFirestore

class BairrooApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            this,
            BairroooDatabase::class.java,
            "bairrooo_database"
        ).build()
    }
    val cartRepository by lazy {
        CartRepository(database.cartDao())
    }
    val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val orderRepository by lazy {
        OrderRepository(firestore)
    }
}
