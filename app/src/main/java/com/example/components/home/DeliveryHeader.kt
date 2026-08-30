package com.example.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.BairrooAddress

@Composable
fun DeliveryHeader(
    addresses: List<BairrooAddress>,
    onAddAddress: () -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNotificationClick: () -> Unit
) {
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
                        color = if (darkTheme) Color(0xFF374151) else Color(0xFFF1F5F9),
                        shape = CircleShape
                    )
                    .clickable(onClick = onToggleTheme),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (darkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
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
                        color = if (darkTheme) Color(0xFF374151) else Color(0xFFF1F5F9),
                        shape = CircleShape
                    )
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(20.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
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
