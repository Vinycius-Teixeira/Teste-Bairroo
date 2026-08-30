package com.example.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PromotionsSection(promotions: List<String>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Promoções", modifier = Modifier.padding(bottom = 8.dp))
        promotions.forEach { promo ->
            Text(text = promo, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}
