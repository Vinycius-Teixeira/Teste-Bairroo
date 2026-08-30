package com.example.repository

import com.example.data.database.CartDao
import com.example.data.database.CartItemEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {
    val cartItems: Flow<List<CartItemEntity>> = cartDao.getAll()
    suspend fun insertOrUpdate(item: CartItemEntity) {
        cartDao.insertOrUpdate(item)
    }
    suspend fun deleteById(id: String) {
        cartDao.deleteById(id)
    }
    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
