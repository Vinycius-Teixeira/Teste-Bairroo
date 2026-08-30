package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.OrderRepository
import com.example.repository.WalletRepository
import com.example.repository.AdminSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminDashboardViewModel : ViewModel() {
    private val orderRepository = OrderRepository.instance
    private val walletRepository = WalletRepository()
    private val settingsRepository = AdminSettingsRepository()
    
    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance = _totalBalance.asStateFlow()
    
    private val _totalCollected = MutableStateFlow(0.0)
    val totalCollected = _totalCollected.asStateFlow()
    
    private val _totalSpentOnDelivery = MutableStateFlow(0.0)
    val totalSpentOnDelivery = _totalSpentOnDelivery.asStateFlow()

    init {
        viewModelScope.launch {
            val wallets = walletRepository.getAllWallets()
            val transactions = walletRepository.getAllTransactions()
            
            _totalBalance.value = wallets.sumOf { it.balance }
            _totalCollected.value = transactions.filter { it.type == "Recharge" }.sumOf { it.amount }
            _totalSpentOnDelivery.value = transactions.filter { it.type == "Deduction" }.sumOf { Math.abs(it.amount) }
        }
    }
    
    val orders = orderRepository.getOrders()

    val totalOrdersToday = orders.size
    val deliveredOrders = orders.filter { it.status == "Entregue" }.size
    val cancelledOrders = orders.filter { it.status == "Cancelado" }.size
    
    // Placeholder for other KPIs, need to fetch users/restaurants
    val activeCustomers = 0 // Needs to be fetched
    val activeStores = 0 // Needs to be fetched
    val onlineDrivers = 0 // Needs to be fetched
    val totalRevenue = orders.sumOf { it.totalPrice }
}
