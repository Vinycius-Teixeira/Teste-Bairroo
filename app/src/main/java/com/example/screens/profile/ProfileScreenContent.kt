package com.example.screens.profile

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.User
import com.example.models.StoreOrder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.TextStyle
import com.example.models.BairrooAddress
import kotlinx.coroutines.launch
import com.example.BairrooLogisticsSettings
import com.example.BairrooOrderRegistry
import com.example.models.CancellationRecord
import androidx.compose.ui.platform.testTag

import com.example.screenFramePadding

@Composable
fun ProfileScreenContent(
    currentRole: String,
    userName: String,
    userEmail: String,
    customerOrders: List<StoreOrder>,
    onOpenExperience: (String) -> Unit,
    onLogoutClick: () -> Unit,
    usersList: List<User>,
    loggedUserId: String,
    onRoleChange: (String) -> Unit,
    onUserUpdated: (User) -> Unit
) {
    var expandedOrders by remember { mutableStateOf(false) }
    var expandedAddresses by remember { mutableStateOf(false) }
    var expandedFavorites by remember { mutableStateOf(false) }
    var expandedCoupons by remember { mutableStateOf(false) }
    var expandedSettings by remember { mutableStateOf(false) }
    var expandedUpgrade by remember { mutableStateOf(false) }

    // Address construction form states
    var showAddAddressForm by remember { mutableStateOf(false) }
    var newCep by remember { mutableStateOf("") }
    var newStreet by remember { mutableStateOf("") }
    var newNumber by remember { mutableStateOf("") }
    var newComplement by remember { mutableStateOf("") }
    var newReference by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("Casa") }
    var addressFormError by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val scopeUser = remember(usersList, loggedUserId) {
        usersList.find { it.id == loggedUserId }
    }

    val activeUserName = scopeUser?.name ?: userName
    val activeUserEmail = scopeUser?.email ?: userEmail

    // Bairroo Mais configuration State
    var isSubscribedToMais by rememberSaveable(scopeUser) {
        mutableStateOf(scopeUser?.bairrooMais == true || scopeUser?.id == "1") // rodrigo active by default
    }

    // PIX Payment States
    var showPixPayment by remember { mutableStateOf(false) }

    if (showPixPayment) {
        AlertDialog(
            onDismissRequest = { showPixPayment = false },
            title = { Text("Pagamento PIX") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Escaneie o QR Code abaixo para assinar o Bairrooo Mais por R$ 9,90/mês.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.size(150.dp).background(Color.LightGray), contentAlignment = Alignment.Center) { 
                        Text("QR CODE") 
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("PIX Copia e Cola: 00020126360014br.gov.bcb.pix...", fontSize = 10.sp)
                }
            },
            confirmButton = {
                Button(onClick = {
                    showPixPayment = false
                    isSubscribedToMais = true
                    if (scopeUser != null) {
                        onUserUpdated(scopeUser.copy(bairrooMais = true))
                    }
                }) {
                    Text("Confirmar Simulação")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPixPayment = false }) { Text("Cancelar") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Professional Header Profile Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large styled Avatar image visual using Canvas or photo emoji selector
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color(0xFF14532D).copy(alpha = 0.08f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val userPhoto = scopeUser?.photo
                    val isEmojiPhoto = userPhoto?.isNotEmpty() == true
                    if (isEmojiPhoto) {
                        Text(
                            text = userPhoto!!,
                            fontSize = 44.sp
                        )
                    } else {
                        Text(
                            text = activeUserName.take(2).uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF14532D)
                            )
                        )
                    }

                    // Visual badge indicating roles
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .background(
                                when (currentRole) {
                                    "admin" -> Color(0xFFEAB308)
                                    "partner" -> Color(0xFFFB923C)
                                    "driver" -> Color(0xFF3B82F6)
                                    else -> Color(0xFF22C55E)
                                },
                                CircleShape
                            )
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                "admin" -> Icons.Default.Shield
                                "partner" -> Icons.Default.Storefront
                                "driver" -> Icons.Default.TwoWheeler
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Text(
                    text = activeUserName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.padding(top = 12.dp)
                )

                Text(
                    text = activeUserEmail,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (scopeUser?.phone?.isNotEmpty() == true) {
                    Text(
                        text = "Contato Celular: ${scopeUser.phone}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF475569), fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (isSubscribedToMais) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(12.dp))
                            Text(
                                "CLIENTE BAIRROO MAIS ⭐",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = Color(0xFFB45309), fontSize = 9.sp)
                            )
                        }
                    }
                }

                // High-visibility structural Role Badge
                Surface(
                    color = when (currentRole) {
                        "admin" -> Color(0xFFFEF3C7)
                        "partner" -> Color(0xFFFFEDD5)
                        "driver" -> Color(0xFFDBEAFE)
                        else -> Color(0xFFDCFCE7)
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = when (currentRole) {
                            "admin" -> "Super Administrador"
                            "partner" -> "Parceiro Lojista"
                            "driver" -> "Parceiro Entregador"
                            else -> "Cliente Bairroo"
                        }.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = when (currentRole) {
                                "admin" -> Color(0xFFB45309)
                                "partner" -> Color(0xFFC2410C)
                                "driver" -> Color(0xFF1D4ED8)
                                else -> Color(0xFF15803D)
                            },
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // SMART LOGIN PROFILE SELECTOR (Multiple internal active roles switcher)
                if (scopeUser != null && scopeUser.roles.size > 1) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "ALTERNAR PERFIL DA CONTA:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scopeUser.roles.forEach { role ->
                            val isCurrent = currentRole == (if (role == "client") "customer" else role)
                            val (label, containerColor) = when(role) {
                                "client" -> Pair("Cliente", Color(0xFF14532D))
                                "partner" -> Pair("Lojista", Color(0xFFC2410C))
                                "driver" -> Pair("Entregador", Color(0xFF1D4ED8))
                                else -> Pair(role.uppercase(), Color(0xFF475569))
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isCurrent) containerColor else Color(0xFFF1F5F9),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isCurrent) Color.Transparent else Color(0xFFE2E8F0),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onRoleChange(role) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isCurrent) Color.White else Color(0xFF475569),
                                    style = TextStyle(fontWeight = FontWeight.Black, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------------
        // MENU ACESSOS ACCORDING TO ROLE MANDATE
        // --------------------------------------------------------------------------
        Text(
            text = "MINHAS OPERAÇÕES & ACESSOS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        // Render role panels
        when (currentRole) {
            "customer" -> {
                // Expanded Customer Screens implementation
                
                // 1. MEUS ENDEREÇOS CADASTRAIS (DYNAMIC EDITABLE LIST)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedAddresses = !expandedAddresses }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFB923C))
                                Column {
                                    Text("Meus Endereços", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                    val count = scopeUser?.addresses?.size ?: 0
                                    Text("$count local(is) cadastrado(s)", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                                }
                            }
                            Icon(
                                imageVector = if (expandedAddresses) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8)
                            )
                        }

                        if (expandedAddresses) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                
                                val activeAddresses = scopeUser?.addresses ?: emptyList()
                                if (activeAddresses.isEmpty()) {
                                    Text(
                                        "Você não tem endereços salvos. Cadastre um endereço para começar a comprar!",
                                        style = TextStyle(color = Color(0xFF94A3B8), fontSize = 11.sp),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                } else {
                                    activeAddresses.forEach { addr ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                            border = BorderStroke(1.dp, if (addr.isPrimary) Color(0xFF14532D) else Color(0xFFE2E8F0))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Text(
                                                            text = "[${addr.type}]",
                                                            style = TextStyle(fontWeight = FontWeight.Black, fontSize = 10.sp, color = Color(0xFFFB923C))
                                                        )
                                                        if (addr.isPrimary) {
                                                            Text(
                                                                text = "(Principal)",
                                                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color(0xFF14532D))
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = "${addr.street}, ${addr.number}",
                                                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B))
                                                    )
                                                    Text(
                                                        text = "CEP: ${addr.cep} • ${addr.complement}",
                                                        style = TextStyle(fontSize = 10.sp, color = Color(0xFF64748B))
                                                    )
                                                    if (addr.reference.isNotEmpty()) {
                                                        Text(
                                                            text = "Ref: ${addr.reference}",
                                                            style = TextStyle(fontSize = 9.sp, color = Color(0xFF64748B), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                                        )
                                                    }
                                                }
                                                
                                                if (!addr.isPrimary && scopeUser != null) {
                                                    TextButton(
                                                        onClick = {
                                                            val updatedAddresses = activeAddresses.map {
                                                                it.copy(isPrimary = it.id == addr.id)
                                                            }
                                                            val updatedU = scopeUser.copy(addresses = updatedAddresses)
                                                            onUserUpdated(updatedU)
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                    ) {
                                                        Text("Tornar Principal", style = TextStyle(fontSize = 10.sp, color = Color(0xFF14532D), fontWeight = FontWeight.Bold))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                
                                if (!showAddAddressForm) {
                                    Button(
                                        onClick = { showAddAddressForm = true },
                                        modifier = Modifier.fillMaxWidth().height(42.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("CADASTRAR NOVO ENDEREÇO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    // INLINE ADDITION FORM WITH MAP INTEGRATION & GPS BUTTON
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Novo Endereço", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp))
                                                
                                                // GPS Auto-location simulation
                                                IconButton(
                                                    onClick = {
                                                        newCep = "35500-120"
                                                        newStreet = "Avenida Getúlio Vargas"
                                                        newNumber = "805"
                                                        newComplement = "Sala 3"
                                                        newReference = "Ao lado do banco"
                                                    }
                                                ) {
                                                    Icon(Icons.Default.MyLocation, contentDescription = "Auto GPS", tint = Color(0xFFC2410C))
                                                }
                                            }

                                            OutlinedTextField(
                                                value = newCep,
                                                onValueChange = { newCep = it },
                                                label = { Text("CEP") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF14532D))
                                            )

                                            OutlinedTextField(
                                                value = newStreet,
                                                onValueChange = { newStreet = it },
                                                label = { Text("Rua / Avenida") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF14532D))
                                            )

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedTextField(
                                                    value = newNumber,
                                                    onValueChange = { newNumber = it },
                                                    label = { Text("Nº") },
                                                    modifier = Modifier.weight(1f),
                                                    singleLine = true,
                                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF14532D))
                                                )
                                                OutlinedTextField(
                                                    value = newComplement,
                                                    onValueChange = { newComplement = it },
                                                    label = { Text("Compl.") },
                                                    modifier = Modifier.weight(1.5f),
                                                    singleLine = true,
                                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF14532D))
                                                )
                                            }

                                            OutlinedTextField(
                                                value = newReference,
                                                onValueChange = { newReference = it },
                                                label = { Text("Referência") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF14532D))
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)).padding(2.dp),
                                                horizontalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                listOf("Casa", "Trabalho", "Outro").forEach { type ->
                                                    val sel = newType == type
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .background(if (sel) Color(0xFF14532D) else Color.Transparent, RoundedCornerShape(6.dp))
                                                            .clickable { newType = type }
                                                            .padding(vertical = 6.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(type, color = if (sel) Color.White else Color(0xFF475569), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            if (addressFormError.isNotEmpty()) {
                                                Text(addressFormError, color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = { showAddAddressForm = false },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Cancelar")
                                                }
                                                Button(
                                                    onClick = {
                                                        if (newCep.trim().isEmpty() || newStreet.trim().isEmpty()) {
                                                            addressFormError = "CEP e Rua são obrigatórios!"
                                                            return@Button
                                                        }
                                                        
                                                        val finalAddr = BairrooAddress(
                                                            id = "addr_" + System.currentTimeMillis(),
                                                            type = newType,
                                                            cep = newCep,
                                                            street = newStreet,
                                                            number = if (newNumber.isEmpty()) "S/N" else newNumber,
                                                            complement = newComplement,
                                                            reference = newReference,
                                                            isPrimary = activeAddresses.isEmpty() // principal if list is currently empty
                                                        )
                                                        
                                                        val updatedAddresses = activeAddresses + finalAddr
                                                        if (scopeUser != null) {
                                                            onUserUpdated(scopeUser.copy(addresses = updatedAddresses))
                                                        }
                                                        
                                                        // Reset form states
                                                        newCep = ""
                                                        newStreet = ""
                                                        newNumber = ""
                                                        newComplement = ""
                                                        newReference = ""
                                                        addressFormError = ""
                                                        showAddAddressForm = false
                                                    },
                                                    modifier = Modifier.weight(1.2f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D))
                                                ) {
                                                    Text("Salvar")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. MEUS FAVORITOS SECTION (INTERACTIVE HEART SELECTOR CARD)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedFavorites = !expandedFavorites }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444))
                                Column {
                                    Text("Meus Favoritos", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                    val favCount = scopeUser?.favorites?.size ?: 0
                                    Text("$favCount item(ns) favorito(s)", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                                }
                            }
                            Icon(
                                imageVector = if (expandedFavorites) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8)
                            )
                        }

                        if (expandedFavorites) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                val favoritesList = scopeUser?.favorites ?: emptyList()
                                if (favoritesList.isEmpty()) {
                                    Surface(
                                        color = Color(0xFFFFF7ED),
                                        border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "Você não favoritou nenhum prato ou estabelecimento! Marque itens com o ícone de coração nas listas de restaurantes.",
                                            style = TextStyle(fontSize = 11.sp, color = Color(0xFFC2410C), fontWeight = FontWeight.Medium),
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                } else {
                                    favoritesList.forEach { itemId ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("🍕", fontSize = 24.sp)
                                                Column {
                                                    Text(
                                                        text = "Prato nº $itemId",
                                                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B))
                                                    )
                                                    Text(
                                                        text = "Favoritado no Bairroo",
                                                        style = TextStyle(fontSize = 10.sp, color = Color(0xFF64748B))
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = {
                                                    val updatedFavorites = favoritesList.filter { it != itemId }
                                                    if (scopeUser != null) {
                                                        onUserUpdated(scopeUser.copy(favorites = updatedFavorites))
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.Favorite, contentDescription = "Unfavorite", tint = Color(0xFFEF4444))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. BAIRROO MAIS (DASHBOARD MEMBERSHIP STATUS INDICATOR CARDS)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("⭐", fontSize = 26.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bairroo Mais", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                Text("Acesso exclusivo a frete grátis e pontos adicionais duplo!", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                            }
                        }
                        
                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        if (isSubscribedToMais) {
                            Surface(
                                color = Color(0xFFECFDF5),
                                border = BorderStroke(1.dp, Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Plano Ativo • Renovação em 14/07", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF047857), fontSize = 11.sp))
                                        Text("Assinatura integrada via Pix", style = TextStyle(color = Color(0xFF065F46), fontSize = 10.sp))
                                    }
                                    TextButton(
                                        onClick = {
                                            isSubscribedToMais = false
                                            if (scopeUser != null) {
                                                onUserUpdated(scopeUser.copy(bairrooMais = false))
                                            }
                                        }
                                    ) {
                                        Text("Cancelar plano", style = TextStyle(fontSize = 10.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Tenha Frete Grátis ilimitado em todos os restaurantes da sua Cidade por apenas R$ 9,90/mês!",
                                    style = TextStyle(fontSize = 11.sp, color = Color(0xFF475569))
                                )
                                Button(
                                    onClick = {
                                        showPixPayment = true
                                    },
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("ASSINAR BAIRROO MAIS - R$ 9,90/mês", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.White))
                                }
                            }
                        }
                    }
                }

                // 4. MEUS CUPONS DE DESCONTO
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedCoupons = !expandedCoupons }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.PointOfSale, contentDescription = null, tint = Color(0xFF14532D))
                                Column {
                                    Text("Meus Cupons", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                    Text("Ver códigos promocionais disponíveis", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                                }
                            }
                            Icon(
                                imageVector = if (expandedCoupons) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8)
                            )
                        }

                        if (expandedCoupons) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                
                                val cupoms = listOf(
                                    Pair("BEMVINDO20", "R$ 20,00 de desconto no primeiro pedido"),
                                    Pair("SOUDOBAIRRO", "Frete Grátis promocional para entrega em 5km"),
                                    Pair("COMERBEM", "15% OFF em restaurantes selecionados da Zona Leste")
                                )

                                cupoms.forEach { (code, desc) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFECFDF5), RoundedCornerShape(10.dp))
                                            .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(10.dp))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(code, style = TextStyle(fontWeight = FontWeight.Black, color = Color(0xFF047857), fontSize = 14.sp))
                                            Text(desc, style = TextStyle(color = Color(0xFF065F46), fontSize = 10.sp))
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    // Copy simulation banner trigger
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, Color(0xFF059669)),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Text("Ativar", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF059669), fontSize = 11.sp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. CONFIGURAÇÕES & PRIVACIDADE SELECTOR
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedSettings = !expandedSettings }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF64748B))
                                Column {
                                    Text("Configurações", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                    Text("Tema visual, notificações, privacidade e suporte", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                                }
                            }
                            Icon(
                                imageVector = if (expandedSettings) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8)
                            )
                        }

                        if (expandedSettings) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Notificações via WhatsApp", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155)))
                                    var alertsActive by remember { mutableStateOf(true) }
                                    Switch(checked = alertsActive, onCheckedChange = { alertsActive = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF14532D)))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Localização em Tempo Real (GPS)", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155)))
                                    var gpsActive by remember { mutableStateOf(true) }
                                    Switch(checked = gpsActive, onCheckedChange = { gpsActive = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF14532D)))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Simulação Modo Dark", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155)))
                                    var darkActive by remember { mutableStateOf(false) }
                                    Switch(checked = darkActive, onCheckedChange = { darkActive = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF14532D)))
                                }
                            }
                        }
                    }
                }

                // 6. DYNAMIC UPGRADE DE CONTA FLOW (SEJA UM PARCEIRO)
                // O sistema ativa a nova função na mesma conta dele, sem exigir criação de outra!
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    border = BorderStroke(1.5.dp, Color(0xFF14532D))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedUpgrade = !expandedUpgrade }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF14532D))
                                Column {
                                    Text("Seja Parceiro do Bairroo 💰", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = Color(0xFF14532D)))
                                    Text("Trabalhe conosco na mesma conta!", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF047857)))
                                }
                            }
                            Icon(
                                imageVector = if (expandedUpgrade) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF14532D)
                            )
                        }

                        if (expandedUpgrade) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFA7F3D0))
                                
                                Text(
                                    "Deseja faturar utilizando a plataforma Bairroo? Você pode cadastrar sua própria loja comercial ou rodar como entregador autônomo na rede na mesma conta, sem complicação de múltiplos logins!",
                                    style = TextStyle(fontSize = 11.sp, color = Color(0xFF065F46))
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Partner Store upgrade option
                                    Button(
                                        onClick = {
                                            if (scopeUser != null) {
                                                val existing = scopeUser.roles
                                                val updatedRoles = if (existing.contains("partner")) existing else existing + "partner"
                                                val updatedU = scopeUser.copy(roles = updatedRoles, role = "partner")
                                                onUserUpdated(updatedU)
                                                onRoleChange("partner") // Switch live!
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2410C)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Text("SOU LOJISTA", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Driver partner upgrade option
                                    Button(
                                        onClick = {
                                            if (scopeUser != null) {
                                                val existing = scopeUser.roles
                                                val updatedRoles = if (existing.contains("driver")) existing else existing + "driver"
                                                val updatedU = scopeUser.copy(roles = updatedRoles, role = "driver")
                                                onUserUpdated(updatedU)
                                                onRoleChange("driver") // Switch live!
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Text("SOU MOTORISTA", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Collapsible section: Meus Pedidos
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedOrders = !expandedOrders }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = null, tint = Color(0xFF22C55E))
                                Column {
                                    Text("Meus Pedidos", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                    Text("Histórico de compras e andamento", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                                }
                            }
                            Icon(
                                imageVector = if (expandedOrders) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8)
                            )
                        }

                        if (expandedOrders) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                if (customerOrders.isEmpty()) {
                                    Text("Você ainda não possui pedidos recentes.", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8)))
                                } else {
                                    customerOrders.forEach { order ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(order.itemsSummary, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                                        Text("Total: R$ ${String.format("%.2f", order.totalPrice)} • Pedido em ${order.time}", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                                                    }
                                                    Surface(
                                                        color = when(order.status) {
                                                            "Entregue" -> Color(0xFFDCFCE7)
                                                            "Em Preparo", "Pendente" -> Color(0xFFFFEDD5)
                                                            "Devolvido para Loja" -> Color(0xFFFEE2E2)
                                                            else -> Color(0xFFDBEAFE)
                                                        },
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            order.status,
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontWeight = FontWeight.Black,
                                                                color = when(order.status) {
                                                                    "Entregue" -> Color(0xFF15803D)
                                                                    "Em Preparo", "Pendente" -> Color(0xFFC2410C)
                                                                    "Devolvido para Loja" -> Color(0xFF991B1B)
                                                                    else -> Color(0xFF1D4ED8)
                                                                },
                                                                fontSize = 9.sp
                                                            ),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                
                                                // MÓDULO DE DEVOLUÇÃO / CLIENTE AUSENTE CHOICES
                                                if (order.status == "Devolvido para Loja") {
                                                    var selectedChoice by remember { mutableStateOf<String?>(null) } // "NONE", "REDELIVER", "PICKUP", "CANCEL"
                                                    var selectedRedeliveryType by remember { mutableStateOf<String?>(null) } // "SAME", "NEW"
                                                    var showSuccessMessage by remember { mutableStateOf<String?>(null) }
                                                    
                                                    Divider(color = Color(0xFFE2E8F0))
                                                    
                                                    if (showSuccessMessage != null) {
                                                        Surface(
                                                            color = Color(0xFFDCFCE7),
                                                            shape = RoundedCornerShape(6.dp),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Text(
                                                                showSuccessMessage!!,
                                                                color = Color(0xFF15803D),
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                modifier = Modifier.padding(8.dp)
                                                            )
                                                        }
                                                    } else {
                                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                            Surface(color = Color(0xFFFEF2F2), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(6.dp)) {
                                                                Column(modifier = Modifier.padding(8.dp)) {
                                                                    Text("⚠️ SEU PEDIDO RETORNOU À LOJA", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF991B1B))
                                                                    Text("Nossa equipe de entrega esteve em seu endereço, mas não obteve contato após as tentativas de chamada.", fontSize = 10.sp, color = Color(0xFF7F1D1D))
                                                                }
                                                            }
                                                            
                                                            Text("Selecione como quer prosseguir:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                                                            
                                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                                                Button(
                                                                    onClick = { selectedChoice = "REDELIVER" },
                                                                    modifier = Modifier.weight(1f),
                                                                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedChoice == "REDELIVER") Color(0xFF0F532D) else Color(0xFFE2E8F0)),
                                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                                                ) {
                                                                    Text("Nova Entrega", fontSize = 10.sp, color = if (selectedChoice == "REDELIVER") Color.White else Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                                                                }
                                                                Button(
                                                                    onClick = { selectedChoice = "PICKUP" },
                                                                    modifier = Modifier.weight(1f),
                                                                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedChoice == "PICKUP") Color(0xFF0F532D) else Color(0xFFE2E8F0)),
                                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                                                ) {
                                                                    Text("Retirar na Loja", fontSize = 10.sp, color = if (selectedChoice == "PICKUP") Color.White else Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                                                                }
                                                                Button(
                                                                    onClick = { selectedChoice = "CANCEL" },
                                                                    modifier = Modifier.weight(1f),
                                                                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedChoice == "CANCEL") Color(0xFFEF4444) else Color(0xFFE2E8F0)),
                                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                                                ) {
                                                                    Text("Cancelar", fontSize = 10.sp, color = if (selectedChoice == "CANCEL") Color.White else Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                                                                }
                                                            }
                                                            
                                                            // CHOICE DYNAMICS
                                                            when (selectedChoice) {
                                                                "REDELIVER" -> {
                                                                    Column(modifier = Modifier.background(Color.White).padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                                        Text("OPÇÕES DE REAGENDAMENTO (Custos Transparentes):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F532D))
                                                                        
                                                                        Card(
                                                                            modifier = Modifier.fillMaxWidth().clickable { selectedRedeliveryType = "SAME" },
                                                                            colors = CardDefaults.cardColors(containerColor = if (selectedRedeliveryType == "SAME") Color(0xFFECFDF5) else Color(0xFFF1F5F9)),
                                                                            border = BorderStroke(1.dp, if (selectedRedeliveryType == "SAME") Color(0xFF059669) else Color(0xFFCBD5E1))
                                                                        ) {
                                                                            Column(modifier = Modifier.padding(8.dp)) {
                                                                                Text("1. Mesmo Entregador (Taxa Reduzida)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                                                Text("Custo da corrida: R$ ${String.format("%.2f", BairrooLogisticsSettings.reducedDeliveryFee.value)}", fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                                                                Text("Seu entregador aguarda por perto e retornará sua entrega com desconto.", fontSize = 9.sp, color = Color(0xFF64748B))
                                                                            }
                                                                        }
                                                                        
                                                                        Card(
                                                                            modifier = Modifier.fillMaxWidth().clickable { selectedRedeliveryType = "NEW" },
                                                                            colors = CardDefaults.cardColors(containerColor = if (selectedRedeliveryType == "NEW") Color(0xFFEFF6FF) else Color(0xFFF1F5F9)),
                                                                            border = BorderStroke(1.dp, if (selectedRedeliveryType == "NEW") Color(0xFF2563EB) else Color(0xFFCBD5E1))
                                                                        ) {
                                                                            Column(modifier = Modifier.padding(8.dp)) {
                                                                                Text("2. Outro Entregador (Taxa Padrão)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                                                Text("Custo da corrida: R$ ${String.format("%.2f", BairrooLogisticsSettings.regularDeliveryFee.value)}", fontSize = 10.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                                                                                Text("Solicita um motorista secundário na rede para redespachar os produtos.", fontSize = 9.sp, color = Color(0xFF64748B))
                                                                            }
                                                                        }
                                                                        
                                                                        if (selectedRedeliveryType != null) {
                                                                            val cost = if (selectedRedeliveryType == "SAME") BairrooLogisticsSettings.reducedDeliveryFee.value else BairrooLogisticsSettings.regularDeliveryFee.value
                                                                            
                                                                            Row(
                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                                verticalAlignment = Alignment.CenterVertically
                                                                            ) {
                                                                                Icon(Icons.Default.Info, null, tint = Color(0xFFFB923C), modifier = Modifier.size(16.dp))
                                                                                Text(
                                                                                    "Prazo máximo de reagendamento: ${BairrooLogisticsSettings.reschedulingLimitHours.value} horas.",
                                                                                    fontSize = 9.sp,
                                                                                    fontWeight = FontWeight.Bold,
                                                                                    color = Color(0xFFC2410C)
                                                                                )
                                                                            }
                                                                            
                                                                            Button(
                                                                                onClick = {
                                                                                    // Apply status update
                                                                                    BairrooOrderRegistry.updateStatus(order.id, "Confirmado: Reagendado")
                                                                                    showSuccessMessage = "Reagendamento confirmado com sucesso! Nova taxa de R$ ${String.format("%.2f", cost)} inclusa. Aguarde o envio!"
                                                                                },
                                                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)),
                                                                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                                                                shape = RoundedCornerShape(6.dp)
                                                                            ) {
                                                                                Text("Confirmar Reagendamento (Pagar R$ ${String.format("%.2f", cost)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                "PICKUP" -> {
                                                                    Column(modifier = Modifier.background(Color.White).padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                                        Text("🛍️ RETIRADA DIRETA NO ESTABELECIMENTO:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F532D))
                                                                        Text("Você pode retirar fisicamente seu pedido diretamente na 'Vila Burger'. Nenhuma cobrança extra de logística será realizada.", fontSize = 10.sp, color = Color(0xFF475569))
                                                                        Text("Endereço: Avenida Central do Bairro, 1022 - Centro", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                                                        
                                                                        Button(
                                                                            onClick = {
                                                                                BairrooOrderRegistry.updateStatus(order.id, "Retirada na Loja Pendente")
                                                                                showSuccessMessage = "Confirmado! Vá até o estabelecimento e informe seu nome: 'Rodrigo Silva' para receber o produto de forma rápida."
                                                                            },
                                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)),
                                                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                                                            shape = RoundedCornerShape(6.dp)
                                                                        ) {
                                                                            Text("Confirmar Retirada Física Grátis", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                        }
                                                                    }
                                                                }
                                                                "CANCEL" -> {
                                                                    Column(modifier = Modifier.background(Color.White).padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                                        val finePercent = BairrooLogisticsSettings.cancellationFinePercent.value
                                                                        val fineVal = order.totalPrice * (finePercent / 100.0)
                                                                        val refundVal = order.totalPrice - fineVal
                                                                        
                                                                        Text("MULTA DE CANCELAMENTO POR AUSÊNCIA:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                                                                        Text("Como os alimentos foram prontamente preparados e a entrega realizada, a desistência retém uma multa de compensação de $finePercent%.", fontSize = 10.sp, color = Color(0xFF475569))
                                                                        
                                                                        Surface(color = Color(0xFFFFF1F2), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                                                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                                Text("• Valor do Pedido: R$ ${String.format("%.2f", order.totalPrice)}", fontSize = 10.sp, color = Color(0xFF991B1B))
                                                                                Text("• Multa ($finePercent%): R$ ${String.format("%.2f", fineVal)} (50% Loja / 50% Bairroo)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                                                                                Text("• Reembolso Estimado: R$ ${String.format("%.2f", refundVal)}", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                                                            }
                                                                        }
                                                                        
                                                                        Button(
                                                                            onClick = {
                                                                                BairrooOrderRegistry.updateStatus(order.id, "Cancelado com Multa")
                                                                                
                                                                                // Divide Fine: 50% Store, 50% Bairroo
                                                                                val storeCut = fineVal * 0.5
                                                                                BairrooLogisticsSettings.merchantCompensationCredit.value += storeCut
                                                                                
                                                                                // Record Bairroo revenue track
                                                                                BairrooLogisticsSettings.cancellationRevenueRecords.add(
                                                                                    CancellationRecord(
                                                                                        id = "CR_" + System.currentTimeMillis().toString().takeLast(6),
                                                                                        source = "Cliente",
                                                                                        orderId = order.id,
                                                                                        date = "14/06/2026",
                                                                                        totalOrderValue = order.totalPrice,
                                                                                        fineValue = fineVal,
                                                                                        splitLojista = storeCut,
                                                                                        splitBairroo = fineVal - storeCut
                                                                                    )
                                                                                )
                                                                                showSuccessMessage = "Pedido Cancelado. Multa de R$ ${String.format("%.2f", fineVal)} aplicada. R$ ${String.format("%.2f", storeCut)} creditado em compensação ao lojista."
                                                                            },
                                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                                                            shape = RoundedCornerShape(6.dp)
                                                                        ) {
                                                                            Text("Confirmar Cancelamento & Pagar Multa", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ProfileCategoryItem(
                    title = "Meus Favoritos",
                    icon = Icons.Default.Favorite,
                    description = "Ver restaurantes e mercados favoritados no Bairroo."
                ) {
                    // Favoritos dummy
                }
            }
            "partner" -> {
                // Parceiro: Perfil, Minha Loja
                ProfileCategoryItem(
                    title = "Dados da Empresa",
                    icon = Icons.Default.Business,
                    description = "Gerenciar CNPJ, faturamento bancário e endereço oficial."
                ) {}

                // MINHA LOJA SPECIAL BUTTON -> launches Store Dashboard internally
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOpenExperience("STORE_PANEL") },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFFB923C).copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFFF7ED), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFFFB923C))
                            }
                            Column {
                                Text("Minha Loja", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                Text("Acessar Painel: Produtos, Pedidos e Financeiro", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFFB923C))
                    }
                }
            }
            "driver" -> {
                // Entregador: Perfil, Entregas
                ProfileCategoryItem(
                    title = "Dados do Veículo",
                    icon = Icons.Default.DirectionsBike,
                    description = "Carros ou motos, vistorias e documentos de CNH."
                ) {}

                // DRIVER PANEL SPECIAL BUTTON -> launches Driver dashboard internally
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOpenExperience("DRIVER_PANEL") },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFEFF6FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = Color(0xFF3B82F6))
                            }
                            Column {
                                Text("Painel Entregador", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                Text("Corridas abertas, ganhos acumulados e extrato", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF3B82F6))
                    }
                }
            }
            "admin" -> {
                // Admin: Perfil, Administração
                ProfileCategoryItem(
                    title = "Supervisão Global",
                    icon = Icons.Default.Security,
                    description = "Ver atividades globais do servidor e segurança de dados."
                ) {}

                // ADMIN DASHBOARD SPECIAL DISCRETE BUTTON -> launches Admin Area internally
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOpenExperience("ADMIN_PANEL") }
                        .testTag("admin_button"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFF14532D).copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF0FDF4), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color(0xFF14532D))
                            }
                            Column {
                                Text("Administração", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                                Text("Módulo Admin: Dashboard, Lojas, Pedidos e Relatórios", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF14532D))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Fully functional Account Switching (TROCAR CONTA / LOG OUT)
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("logout_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444))
                Text(
                    "LOGOUT / TROCAR DE CONTA",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFEF4444),
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileCategoryItem(
    title: String,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF64748B))
                Column {
                    Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)))
                    Text(description, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
        }
    }
}
