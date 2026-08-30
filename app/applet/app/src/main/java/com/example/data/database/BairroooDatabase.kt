package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class BairroooDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
