package com.example.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OrderPreview(orderTitle: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Último Pedido", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = orderTitle)
    }
}
