package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.*
import com.example.models.Restaurant
import com.example.models.BairrooAddress
import com.example.data.database.CartItemEntity

@Composable
fun CheckoutReviewScreenContent(
    restaurant: Restaurant,
    cartItems: List<CartItemEntity>,
    addresses: List<BairrooAddress>,
    onBackPress: () -> Unit,
    onConfirm: () -> Unit,
    onRemoveItem: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Revisar Pedido", style = MaterialTheme.typography.headlineSmall)
            Text("Loja: ${restaurant.name}")
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cartItems) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text("R$ ${item.price}", style = MaterialTheme.typography.bodyMedium)
                                Text("Qtd: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
                            }
                            Button(onClick = { onRemoveItem(item.id) }) {
                                Text("Remover")
                            }
                        }
                    }
                }
            }
        }
        Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth()) {
            Text("Confirmar Pedido")
        }
        Button(onClick = onBackPress, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar")
        }
    }
}
