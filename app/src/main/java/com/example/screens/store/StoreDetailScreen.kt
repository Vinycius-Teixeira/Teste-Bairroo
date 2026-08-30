package com.example.screens.store

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.example.models.FoodItem
import com.example.models.Restaurant
import com.example.data.database.CartItemEntity
// import com.example.FoodImagePlaceholder // Assuming it's defined elsewhere, I might need to move it too

// ... (imports)

@Composable
fun StoreDetailScreenContent(
    restaurant: Restaurant,
    cartItems: List<CartItemEntity>,
    onAddToCart: (CartItemEntity) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onBackPress: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val storeProducts = com.example.mockFoodItems // mockFoodItems is package-level in MainActivity.kt
    
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        // App Bar / Header
        Surface(color = Color.White, tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackPress) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("${restaurant.category} • ${restaurant.shipping}", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
        
        // Products List
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cardápio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(storeProducts) { foodItem ->
                // Calculate current qty from Room's cartItems
                val currentQty = cartItems.find { it.id == foodItem.id }?.quantity ?: 0
                
                FoodMenuItemCard(
                    foodItem = foodItem,
                    quantity = currentQty,
                    onAdd = { entity -> onAddToCart(entity) },
                    onSubtract = { id -> 
                        if (currentQty > 1) {
                            // Update with currentQty - 1
                            onAddToCart(CartItemEntity(
                                id = foodItem.id,
                                name = foodItem.name,
                                price = foodItem.price,
                                quantity = currentQty - 1,
                                restaurantId = restaurant.id,
                                imageType = foodItem.imageType
                            ))
                        } else if (currentQty == 1) {
                            onRemoveFromCart(id)
                        }
                    },
                    restaurantId = restaurant.id
                )
            }
        }
        
        // Checkout Bar if items in cart
        val cartTotal = cartItems.sumOf { it.price * it.quantity }
        val cartCount = cartItems.sumOf { it.quantity }
        if (cartCount > 0) {
            Surface(color = Color.White, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total ($cartCount itens)", fontSize = 12.sp, color = Color.Gray)
                        Text("R$ ${String.format("%.2f", cartTotal)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F532D))
                    }
                    Button(
                        onClick = onCheckoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Finalizar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodMenuItemCard(
    foodItem: FoodItem,
    quantity: Int,
    onAdd: (CartItemEntity) -> Unit,
    onSubtract: (String) -> Unit,
    restaurantId: String
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(foodItem.name, style = MaterialTheme.typography.titleMedium)
            Text("R$ ${foodItem.price}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Qtd no carrinho: $quantity")
                Row {
                    IconButton(onClick = { onSubtract(foodItem.id) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Remover")
                    }
                    IconButton(onClick = { 
                        onAdd(
                            CartItemEntity(
                                id = foodItem.id,
                                name = foodItem.name,
                                price = foodItem.price,
                                quantity = quantity + 1,
                                restaurantId = restaurantId,
                                imageType = null
                            )
                        ) 
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar")
                    }
                }
            }
        }
    }
}
