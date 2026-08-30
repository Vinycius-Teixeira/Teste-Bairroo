package com.example.screens.store

import androidx.compose.runtime.*
import com.example.models.Restaurant
import com.example.models.BairrooAddress
import com.example.models.FoodItem
import com.example.data.database.CartItemEntity

@Composable
fun StoreWrapperScreen(
    restaurant: Restaurant,
    cartItems: List<CartItemEntity>,
    onAddToCart: (CartItemEntity) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    addresses: List<BairrooAddress>,
    onBackPress: () -> Unit
) {
    // [Implementation here]
    // Was in MainActivity before, needs StoreDetailScreenContent, CheckoutFlow
}
