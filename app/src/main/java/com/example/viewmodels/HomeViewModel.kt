package com.example.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _addressState = MutableStateFlow<String>("Loading...")
    val addressState: StateFlow<String> = _addressState

    private val _storeState = MutableStateFlow<List<String>>(emptyList())
    val storeState: StateFlow<List<String>> = _storeState

    private val _categoriesState = MutableStateFlow<List<String>>(emptyList())
    val categoriesState: StateFlow<List<String>> = _categoriesState

    private val _promotionState = MutableStateFlow<List<String>>(emptyList())
    val promotionState: StateFlow<List<String>> = _promotionState

    private val _loadingState = MutableStateFlow<Boolean>(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    // To be implemented: Fetching logic
}
