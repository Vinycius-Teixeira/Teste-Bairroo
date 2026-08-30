package com.example.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.components.home.*
import com.example.viewmodels.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val address by viewModel.addressState.collectAsState()
    val stores by viewModel.storeState.collectAsState()
    val categories by viewModel.categoriesState.collectAsState()
    val promotions by viewModel.promotionState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DeliveryHeader(
                addresses = emptyList(),
                onAddAddress = {},
                darkTheme = false,
                onToggleTheme = {},
                onNotificationClick = { /* TODO: Implement navigation to notifications */ }
            )
        }
        item { SearchSection(onSearch = {}) }
        item { CategorySection(categories = categories) }
        item { PromotionsSection(promotions = promotions) }
        item { NearbyStoresSection(stores = stores) }
        item { OrderPreview(orderTitle = "Último pedido") }
    }
}
