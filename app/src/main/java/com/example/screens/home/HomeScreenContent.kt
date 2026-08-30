package com.example.screens.home

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.User
import com.example.models.Restaurant
import com.example.models.BairrooAddress
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.geometry.Offset
import com.example.mockRestaurants
import androidx.compose.ui.text.TextStyle
import com.example.components.FoodImagePlaceholder
import com.example.BairrooLogo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.vector.ImageVector

import com.example.screenFramePadding

@Composable
fun HomeScreenContent(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToPlus: () -> Unit,
    onNavigateToPartner: () -> Unit,
    onSelectRestaurant: (Restaurant) -> Unit,
    currentUser: User?,
    addresses: List<BairrooAddress>,
    onAddAddress: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Header: Address & Notification Icons in Artistic Flair theme style
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Address Info Block
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "ENTREGAR EM",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "ENTREGAR EM",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280),
                                fontSize = 10.sp,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable { onAddAddress() }
                    ) {
                        Text(
                            text = addresses.firstOrNull { it.isPrimary }?.let { "${it.street}, ${it.number}" } ?: "Adicionar endereço",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Mudar endereço",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                // Action Buttons Row (Theme toggle + Bell Notification in white circular card style)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Accent Dark Mode toggle button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9),
                                shape = CircleShape
                            )
                            .clickable(onClick = onToggleTheme),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Mudar tema",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bell Notification button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9),
                                shape = CircleShape
                            )
                            .clickable { onShowSnackbar("Você não tem novas notificações.") },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(20.dp)) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificações",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // Notification Alert Dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-12).dp, y = 12.dp)
                                .background(Color(0xFFFB923C), CircleShape)
                        )
                    }
                }
            }
        }

        // Brand Logo and "Olá, Rodrigo" capsule
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    BairrooLogo(fontSize = 28.sp)
                    Text(
                        text = "Seu bairro inteiro em um app",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isDark) Color(0xFF22C55E).copy(alpha = 0.2f) else Color(0xFF22C55E).copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Olá, Rodrigo",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF86EFAC) else Color(0xFF14532D)
                        )
                    )
                }
            }
        }

        // Elevated Search bar triggers navigation to Search tab, customized for Artistic Flair Theme
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToSearch)
                    .shadow(1.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "O que você precisa no bairro agora?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Horizontal Promo banner in Artistic Flair deep forest green with orange details
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(134.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Subtle ambient glow in bottom-right
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0xFF22C55E).copy(alpha = 0.2f),
                            radius = 70.dp.toPx(),
                            center = Offset(size.width + 10.dp.toPx(), size.height + 10.dp.toPx())
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1.3f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "ESPECIAL PARA VOCÊ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFFB923C),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.8.sp,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Frete Grátis em\ntodo o bairro.",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    lineHeight = 22.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF22C55E), RoundedCornerShape(8.dp))
                                    .clickable(onClick = onNavigateToSearch)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "APROVEITAR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.7f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            FoodImagePlaceholder(
                                type = "banner_pizza",
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        }
                    }
                }
            }
        }

        // Categories Circular Rows - styled with Forest Green headers
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categorias",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF14532D)
                        )
                    )
                    Text(
                        text = "Ver mais",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF22C55E),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable(onClick = onNavigateToSearch)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item { CategoryItem("Restaurantes", Color(0xFFFEF3C7), Color(0xFFD97706), Icons.Default.Restaurant, onNavigateToSearch) }
                    item { CategoryItem("Mercados", Color(0xFFDCFCE7), Color(0xFF15803D), Icons.Default.LocalMall, onNavigateToSearch) }
                    item { CategoryItem("Farmácias", Color(0xFFFEE2E2), Color(0xFFDC2626), Icons.Default.MedicalServices, onNavigateToSearch) }
                    item { CategoryItem("Bebidas", Color(0xFFFFEDD5), Color(0xFFEA580C), Icons.Default.LocalDrink, onNavigateToSearch) }
                    item { CategoryItem("Conveniência", Color(0xFFE0F2FE), Color(0xFF0284C7), Icons.Default.Storefront, onNavigateToSearch) }
                }
            }
        }

        // Próximos de você - Forest Green header and Ver Mais
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Próximos de você",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF14532D)
                        )
                    )
                    Text(
                        text = "Ver mais",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF22C55E),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable(onClick = onNavigateToSearch)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(mockRestaurants) { rest ->
                        HomeNearCard(restaurant = rest, onClick = { onSelectRestaurant(rest) })
                    }
                }
            }
        }

        // Bairroo Mais mini card - Restyled as a luxury gold badge card in deep forest green
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToPlus)
                    .shadow(1.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Geometric star background watermark on the right side
                    Canvas(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 10.dp, y = (-10).dp)
                    ) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = 60.dp.toPx()
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFFB923C), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "💎",
                                        style = TextStyle(fontSize = 10.sp),
                                        modifier = Modifier.padding(bottom = 1.dp)
                                    )
                                }
                                Text(
                                    text = "Bairroo Plus",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Você está no nível Ouro (2.250 pts)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        
                        // Action button VER BENEFÍCIOS
                        Box(
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .clickable(onClick = onNavigateToPlus),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VER BENEFÍCIOS",
                                style = TextStyle(
                                    color = Color(0xFF14532D),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Seja Parceiro mini-block
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToPartner)
                    .shadow(4.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(
                            "Seja parceiro do Bairroo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            "Cresça com a gente no seu bairro de forma flexível.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onNavigateToPartner,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB923C)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Quero ser parceiro",
                                style = TextStyle(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.7f)
                            .height(84.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        FoodImagePlaceholder("parceiro", modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun CategoryItem(
    label: String,
    bgColor: Color,
    iconColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable(onClick = onClick)
    ) {
        Card(
            modifier = Modifier
                .size(56.dp)
                .shadow(1.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                width = 1.dp,
                color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(bgColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun HomeNearCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                FoodImagePlaceholder(
                    type = restaurant.imageType,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                )
                if (restaurant.coupon.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color(0xFFFB923C), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = restaurant.coupon,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFB923C),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${restaurant.rating}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${restaurant.time}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B7280),
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                
                // Adaptive / Modern status tags instead of simple text
                val isFreeShipping = restaurant.shipping.contains("grátis", true)
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isFreeShipping) {
                                if (isDark) Color(0xFF22C55E).copy(alpha = 0.2f) else Color(0xFF22C55E).copy(alpha = 0.1f)
                            } else {
                                if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isFreeShipping) "Entrega Grátis" else restaurant.shipping,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isFreeShipping) {
                                if (isDark) Color(0xFF86EFAC) else Color(0xFF14532D)
                            } else {
                                Color(0xFF6B7280)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
