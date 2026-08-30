package com.example.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NearbyStoresSection(stores: List<String>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Lojas Próximas", modifier = Modifier.padding(bottom = 8.dp))
        stores.forEach { store ->
            Text(text = store, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}
