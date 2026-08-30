package com.example.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ScreenTab
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.testTag


@Composable
fun BairrooBottomNav(
    activeTab: ScreenTab,
    onTabSelect: (ScreenTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Outlined.Home,
                filledIcon = Icons.Default.Home,
                label = "Início",
                selected = activeTab == ScreenTab.HOME,
                onClick = { onTabSelect(ScreenTab.HOME) },
                testTag = "tab_home"
            )
            BottomNavItem(
                icon = Icons.Outlined.Search,
                filledIcon = Icons.Default.Search,
                label = "Busca",
                selected = activeTab == ScreenTab.SEARCH,
                onClick = { onTabSelect(ScreenTab.SEARCH) },
                testTag = "tab_search"
            )
            BottomNavItem(
                icon = Icons.Outlined.Star,
                filledIcon = Icons.Default.Star,
                label = "Bairroo Mais",
                selected = activeTab == ScreenTab.PLUS,
                onClick = { onTabSelect(ScreenTab.PLUS) },
                testTag = "tab_plus"
            )
            BottomNavItem(
                icon = Icons.Outlined.Person,
                filledIcon = Icons.Default.Person,
                label = "Perfil",
                selected = activeTab == ScreenTab.PROFILE,
                onClick = { onTabSelect(ScreenTab.PROFILE) },
                testTag = "tab_profile"
            )
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    filledIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val duration = 200
    val weightAnim by animateFloatAsState(if (selected) 1.2f else 0.8f, tween(duration))
    val tintColor = if (selected) MaterialTheme.colorScheme.primary else Color(0xFF9CA3AF)

    Box(
        modifier = Modifier
            .weight(weightAnim)
            .fillMaxHeight()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) filledIcon else icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = tintColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
