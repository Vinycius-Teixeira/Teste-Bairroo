package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.BairrooApplication
import com.example.models.StoreOrder
import com.example.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private val _orders = MutableStateFlow<List<StoreOrder>>(emptyList())
    val orders: StateFlow<List<StoreOrder>> = _orders

    fun addOrder(order: StoreOrder) {
        repository.addOrder(order)
        _orders.value = repository.getOrders().toList()
    }

    fun acceptOrder(orderId: String, driverId: String) {
        viewModelScope.launch {
            repository.acceptOrder(orderId, driverId)
            _orders.value = repository.getOrders().toList()
        }
    }

    fun getLiveOrdersForRestaurant(restaurantId: String): StateFlow<List<StoreOrder>> {
        return repository.getLiveOrdersForRestaurant(restaurantId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun createOrder(order: StoreOrder) {
        viewModelScope.launch {
            repository.createOrder(order)
        }
    }


    val availableOrders: StateFlow<List<StoreOrder>> = repository.getAvailableOrders().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getLiveOrderForDriver(driverId: String): StateFlow<StoreOrder?> {
        return repository.getLiveOrderForDriver(driverId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun acceptOrderAsDriver(orderId: String, driverId: String) {
        viewModelScope.launch {
            repository.assignDriverToOrder(orderId, driverId)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun getLiveOrdersForCustomer(customerId: String): StateFlow<List<StoreOrder>> {
        return repository.getLiveOrdersForCustomer(customerId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BairrooApplication
                return OrderViewModel(application.orderRepository) as T
            }
        }
    }
}
