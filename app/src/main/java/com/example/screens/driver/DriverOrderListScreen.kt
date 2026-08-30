package com.example.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.viewmodels.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverOrderListScreen(viewModel: OrderViewModel, driverId: String, onNavigateToWallet: () -> Unit) {
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Pedidos Disponíveis") },
                actions = {
                    IconButton(onClick = onNavigateToWallet) {
                        Text("Carteira")
                    }
                }
            ) 
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(orders.size) { index ->
                val order = orders[index]
                if (order.status == "Pendente") {
                    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Pedido: ${order.id}")
                            Text("Valor: R$ ${order.totalPrice}")
                            Button(onClick = { viewModel.acceptOrder(order.id, driverId) }) {
                                Text("Aceitar")
                            }
                        }
                    }
                }
            }
        }
    }
}
