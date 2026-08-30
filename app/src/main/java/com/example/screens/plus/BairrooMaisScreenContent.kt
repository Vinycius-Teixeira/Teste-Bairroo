package com.example.screens.plus

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screenFramePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle


@Composable
fun BairrooMaisScreenContent(
    points: Int,
    onRedeemCoupon: (cost: Int, successMsg: String) -> Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }
        
        item {
            Text(
                "Bairroo Mais",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )
        }

        // Bank-app styled progress premium card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    "Seu nível atual",
                                    style = TextStyle(color = Color(0xFF94A3B8), fontSize = 12.sp)
                                )
                                Text(
                                    "Prata Premium",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp,
                                        letterSpacing = (-0.5).sp
                                    )
                                )
                            }
                            
                            // Glowing Star badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        Brush.sweepGradient(
                                            colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFFE2E8F0))
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = "Prata",
                                    tint = Color(0xFF1E293B),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Progress bar metrics
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Bronze", style = TextStyle(color = Color(0xFF64748B), fontSize = 11.sp))
                            Text("$points / 3.000 pts para Ouro", style = TextStyle(color = Color(0xFF22C55E), fontSize = 11.sp, fontWeight = FontWeight.Bold))
                            Text("Ouro", style = TextStyle(color = Color(0xFF64748B), fontSize = 11.sp))
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Custom premium progress bar
                        val targetProgress = points.toFloat() / 3000f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF334155))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(targetProgress)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFF22C55E), Color(0xFFFB923C))
                                        )
                                    )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        // Large detailed totals
                        Divider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Pontos acumulados", style = TextStyle(color = Color(0xFF94A3B8), fontSize = 11.sp))
                                Text(
                                    "$points",
                                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                                )
                            }
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Ver benefícios", color = Color.White, style = TextStyle(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }

        // Coupons redemption list section
        item {
            Text(
                "Cupons para você resgatar",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            PlusCouponRedeemCard(
                title = "R$ 10,00 OFF",
                description = "Em pedidos mínimos de R$ 50,00",
                pointsCost = 1000,
                onRedeem = { onRedeemCoupon(1000, "Cupom R$ 10,00 resgatado com sucesso!") }
            )
        }

        item {
            PlusCouponRedeemCard(
                title = "Frete Grátis",
                description = "Em pedidos mínimos de R$ 30,00",
                pointsCost = 800,
                onRedeem = { onRedeemCoupon(800, "Frete Grátis ativo na sua conta!") }
            )
        }
        
        item {
            PlusCouponRedeemCard(
                title = "15% de Desconto",
                description = "Nos parceiros locais premium",
                pointsCost = 1500,
                onRedeem = { onRedeemCoupon(1500, "Desconto de 15% aplicado!") }
            )
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
fun PlusCouponRedeemCard(
    title: String,
    description: String,
    pointsCost: Int,
    onRedeem: () -> Boolean
) {
    var isRedeemed by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Pontos",
                        tint = Color(0xFFFB923C),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "$pointsCost pontos",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFFB923C))
                    )
                }
            }
            Button(
                onClick = {
                    if (!isRedeemed) {
                        val res = onRedeem()
                        if (res) isRedeemed = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRedeemed) Color(0xFFE5E7EB) else Color(0xFF14532D)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isRedeemed) "Resgatado" else "Resgatar",
                    color = if (isRedeemed) Color(0xFF9CA3AF) else Color.White,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
