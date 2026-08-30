package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.database.BairroooDatabase
import com.example.repository.CartRepository

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
}
