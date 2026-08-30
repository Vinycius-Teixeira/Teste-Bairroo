package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.BairrooApplication
import com.example.data.database.CartItemEntity
import com.example.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {
    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addItem(item: CartItemEntity) {
        viewModelScope.launch {
            repository.insertOrUpdate(item)
        }
    }
    fun removeItem(id: String) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BairrooApplication
                return CartViewModel(application.cartRepository) as T
            }
        }
    }
}
