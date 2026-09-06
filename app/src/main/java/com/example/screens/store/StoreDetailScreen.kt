package com.example.screens.store

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.FoodItem
import com.example.models.Restaurant
import com.example.data.database.CartItemEntity
import com.example.components.FoodMenuItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreenContent(
    restaurant: Restaurant,
    cartItems: List<CartItemEntity>,
    onAddToCart: (CartItemEntity) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onCheckoutClick: () -> Unit,
    onBackPress: () -> Unit
) {
    // Mock de produtos temporário (já que ainda não temos o Firestore de produtos completo)
    val mockMenu = listOf(
        FoodItem("1", "Pizza Marguerita", "Molho de tomate, muçarela, manjericão e azeite.", 38.90, "pizza", "Destaques"),
        FoodItem("2", "Pizza Calabresa", "Molho de tomate, muçarela e calabresa.", 36.90, "pizza", "Destaques"),
        FoodItem("3", "Coca-Cola 1L", "Refrescante embalagem de 1 litro.", 8.90, "coca", "Bebidas")
    )

    // Calcula o total do carrinho e a quantidade de itens de forma reativa lendo do Room
    val cartTotal = cartItems.sumOf { it.price * it.quantity }
    val cartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurant.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            // O botão de Checkout só aparece se existir algo salvo no banco local (Room)
            if (cartCount > 0) {
                Surface(
                    color = Color(0xFF14532D),
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    onClick = onCheckoutClick
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$cartCount itens", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text("Ver Carrinho (R$ ${String.format("%.2f", cartTotal)})", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Cardápio", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(mockMenu) { foodItem ->
                // REGRA DE NEGÓCIO CRÍTICA: Lemos a quantidade atual DIRETO do banco de dados
                val cartItem = cartItems.find { it.id == foodItem.id }
                val currentQuantity = cartItem?.quantity ?: 0

                FoodMenuItemCard(
                    foodItem = foodItem,
                    quantity = currentQuantity,
                    onAdd = {
                        // Envia o objeto inteiro com a quantidade somada. O Room vai fazer o REPLACE usando o ID.
                        onAddToCart(
                            CartItemEntity(
                                id = foodItem.id,
                                name = foodItem.name,
                                price = foodItem.price,
                                quantity = currentQuantity + 1,
                                restaurantId = restaurant.id,
                                imageType = foodItem.imageType
                            )
                        )
                    },
                    onSubtract = {
                        if (currentQuantity > 1) {
                            // Diminui 1 no banco
                            onAddToCart(
                                CartItemEntity(
                                    id = foodItem.id,
                                    name = foodItem.name,
                                    price = foodItem.price,
                                    quantity = currentQuantity - 1,
                                    restaurantId = restaurant.id,
                                    imageType = foodItem.imageType
                                )
                            )
                        } else if (currentQuantity == 1) {
                            // Remove do banco se a quantidade zerar
                            onRemoveFromCart(foodItem.id)
                        }
                    }
                )
            }
        }
    }
}
