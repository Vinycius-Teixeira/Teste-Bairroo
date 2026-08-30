package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.DriverWallet
import com.example.models.WalletTransaction
import com.example.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {
    private val _wallet = MutableStateFlow<DriverWallet?>(null)
    val wallet = _wallet.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    fun loadWallet(driverId: String) {
        viewModelScope.launch {
            _wallet.value = repository.getWallet(driverId)
            _transactions.value = repository.getTransactions(driverId)
        }
    }

    fun recharge(driverId: String, amount: Double) {
        viewModelScope.launch {
            repository.processTransaction(driverId, amount, "Recharge", null, "Recarga via PIX")
            loadWallet(driverId)
        }
    }
}
