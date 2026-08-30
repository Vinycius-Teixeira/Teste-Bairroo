package com.example.screens.driver

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.*
// --------------------------------------------------------------------------
// SCREEN 5: PAINEL ENTREGADOR (Interactive order simulator)

// --------------------------------------------------------------------------
// SCREEN 5: PAINEL ENTREGADOR (Interactive order simulator)
// --------------------------------------------------------------------------
@Composable
fun DriverDashboardContent(
    isOnline: Boolean,
    onToggleOnline: (Boolean) -> Unit,
    earnings: Double,
    deliveriesCount: Int,
    activeOffer: Boolean,
    deliveryState: String,
    routeProgress: Float,
    onAcceptOffer: () -> Unit,
    onDeclineOffer: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenFramePadding())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Upper Header Card with Toggle button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Painel do Entregador",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        )
                        Text(
                            text = if (isOnline) "Você está Online e visível" else "Você está Offline",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isOnline) Color(0xFF22C55E) else Color(0xFF6B7280),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Switch(
                        checked = isOnline,
                        onCheckedChange = onToggleOnline,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF22C55E)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Statistics indicators row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Ganhos hoje",
                    value = "R$ ${String.format("%.2f", earnings)}",
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFF22C55E),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Corridas",
                    value = "$deliveriesCount",
                    icon = Icons.Default.ElectricScooter,
                    color = Color(0xFFFB923C),
                    modifier = Modifier.weight(0.9f)
                )
                StatCard(
                    title = "Avaliação",
                    value = "★ 4.9",
                    icon = Icons.Default.Star,
                    color = Color(0xFFFBBF24),
                    modifier = Modifier.weight(0.9f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                "Visão do mapa logístico",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Real Canvas map visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, if (isOnline) Color(0xFF22C55E).copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(20.dp))
            ) {
                MapSimulation(
                    modifier = Modifier.fillMaxSize(),
                    vehicleRouteProgress = routeProgress
                )
                
                // Overlay driver offline status
                if (!isOnline) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Fique online para visualizar o trânsito de pedidos do bairro.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                // Show floating routing alerts
                if (deliveryState == "ACCEPTED") {
                    StatusFloatingPill("Corrida Aceita! Indo coletar...")
                } else if (deliveryState == "PICKUP") {
                    StatusFloatingPill("Pedido coletado! Rota para cliente...")
                } else if (deliveryState == "DELIVERING") {
                    StatusFloatingPill("A caminho do destino...")
                } else if (deliveryState == "COMPLETED") {
                    StatusFloatingPill("Entrega concluída!")
                }
            }
        }

        // Animated Popup Card for Active Delivery proposal
        AnimatedVisibility(
            visible = activeOffer && isOnline,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(16.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "NOVA CORRIDA DISPONÍVEL",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                            )
                        }
                        Text(
                            "R$ 8,50",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = Color(0xFF14532D))
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Pizzaria do Bairro • 1.2 km de distância",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "Retirada: Centro • Destino: Rua das Flores, 123",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280))
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDeclineOffer,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Recusar", color = Color(0xFF6B7280), style = TextStyle(fontWeight = FontWeight.Bold))
                        }
                        Button(
                            onClick = onAcceptOffer,
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                        ) {
                            Text("Aceitar", style = TextStyle(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFloatingPill(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            color = Color(0xFF14532D),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.shadow(8.dp, RoundedCornerShape(12.dp))
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = TextStyle(color = Color(0xFF9CA3AF), fontSize = 10.sp))
            Text(value, style = TextStyle(fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground))
        }
    }
}

// --------------------------------------------------------------------------
// MAP SIMULATION CANVAS DRAWING
// --------------------------------------------------------------------------
@Composable
fun MapSimulation(modifier: Modifier = Modifier, vehicleRouteProgress: Float = 0f) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Grass Background green shades
        drawRect(Color(0xFFE6F4EA))

        // Grid/Road paths (Gray boundaries with inner white fill)
        val roadStrokeBorder = Stroke(width = 28.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val roadStrokeFill = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        val roadPaths = listOf(
            Path().apply {
                moveTo(w * 0.1f, h * 0.2f)
                lineTo(w * 0.9f, h * 0.25f)
                lineTo(w * 0.85f, h * 0.8f)
                lineTo(w * 0.15f, h * 0.75f)
                close()
            },
            Path().apply {
                moveTo(w * 0.5f, h * 0.22f)
                lineTo(w * 0.5f, h * 0.77f)
            },
            Path().apply {
                moveTo(w * 0.12f, h * 0.5f)
                lineTo(w * 0.88f, h * 0.5f)
            }
        )

        // Draw borders first
        roadPaths.forEach { path ->
            drawPath(path, Color(0xFFCBD5E1), style = roadStrokeBorder)
        }
        // Draw inner white fill
        roadPaths.forEach { path ->
            drawPath(path, Color.White, style = roadStrokeFill)
        }

        // Dynamic Active Delivery route from Pizzaria to client (Rua das Flores)
        val startX = w * 0.5f
        val startY = h * 0.5f
        val endX = w * 0.85f
        val endY = h * 0.8f

        // Dotted active path
        val activeRoute = Path().apply {
            moveTo(startX, startY)
            lineTo(w * 0.86f, h * 0.5f)
            lineTo(endX, endY)
        }

        drawPath(
            activeRoute,
            color = Color(0xFF22C55E),
            style = Stroke(
                width = 6.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f),
                cap = StrokeCap.Round
            )
        )

        // Draw Pins
        // Pickup node: Pizzaria (Deep primary green)
        drawCircle(color = Color(0xFF14532D), radius = 11.dp.toPx(), center = Offset(startX, startY))
        drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(startX, startY))

        // Delivery node: Client (Vibrant Orange pin)
        drawCircle(color = Color(0xFFFB923C), radius = 11.dp.toPx(), center = Offset(endX, endY))
        drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(endX, endY))

        // Interactive scooter marker moving in real-time along progress
        if (vehicleRouteProgress > 0f) {
            val midX = w * 0.86f
            val midY = h * 0.5f
            
            val currentX: Float
            val currentY: Float
            
            if (vehicleRouteProgress < 0.5f) {
                val t = vehicleRouteProgress * 2f
                currentX = startX + (midX - startX) * t
                currentY = startY + (midY - startY) * t
            } else {
                val t = (vehicleRouteProgress - 0.5f) * 2f
                currentX = midX + (endX - midX) * t
                currentY = midY + (endY - midY) * t
            }

            // Draw glowing marker
            drawCircle(Color(0xFF22C55E).copy(alpha = 0.35f), radius = 16.dp.toPx(), center = Offset(currentX, currentY))
            drawCircle(Color(0xFF14532D), radius = 8.dp.toPx(), center = Offset(currentX, currentY))
            drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(currentX, currentY))
        }
    }
}
