package com.example.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun FoodImagePlaceholder(type: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawRect(Color(0xFFF1F5F9)) // neutral light background

        when (type) {
            "pizza" -> {
                // Pizza Wedge Drawing
                val pizzaPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.2f)
                    lineTo(w * 0.8f, h * 0.8f)
                    quadraticTo(w * 0.5f, h * 0.85f, w * 0.2f, h * 0.8f)
                    close()
                }
                drawPath(pizzaPath, Color(0xFFFBBF24))
            }
            // Add other cases if needed
        }
    }
}

