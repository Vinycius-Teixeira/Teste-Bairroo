package com.example.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.User

// --------------------------------------------------------------------------
// SCREEN: LOGIN
// --------------------------------------------------------------------------
@Composable
fun LoginScreenContent(
    errorMessage: String?,
    onLoginClick: (String, String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onClearError: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color(0xFF14532D).copy(alpha = 0.04f), radius = 350.dp.toPx(), center = Offset(size.width * 0.1f, size.height * 0.2f))
            drawCircle(color = Color(0xFFFB923C).copy(alpha = 0.04f), radius = 280.dp.toPx(), center = Offset(size.width * 0.9f, size.height * 0.8f))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Bairroo", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black, color = Color(0xFF14532D), letterSpacing = (-1.5).sp))
                Box(modifier = Modifier.size(10.dp).background(Color(0xFFFB923C), CircleShape).offset(y = 10.dp))
            }
            Text(text = "Seu bairro inteiro em um único app", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B), fontWeight = FontWeight.Medium), modifier = Modifier.padding(top = 4.dp, bottom = 32.dp))

            Card(
                modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Identifique-se", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)), modifier = Modifier.padding(bottom = 4.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; onClearError() },
                        label = { Text("E-mail") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth().testTag("login_email"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF14532D), focusedLabelColor = Color(0xFF14532D), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; onClearError() },
                        label = { Text("Senha") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = "Alternar senha", tint = Color(0xFF94A3B8))
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("login_password"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF14532D), focusedLabelColor = Color(0xFF14532D), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    if (!errorMessage.isNullOrEmpty()) {
                        Text(text = errorMessage, color = Color(0xFFEF4444), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Button(
                        onClick = { onLoginClick(email.trim(), password) },
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("login_submit"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ENTRAR NO APP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp))
                    }
                    
                    OutlinedButton(
                        onClick = onCreateAccountClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("login_create_account"),
                        border = BorderStroke(1.5.dp, Color(0xFF14532D)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF14532D)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CRIAR CONTA GRÁTIS", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp))
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------
// SCREEN: CADASTRO CLIENTE (REGISTRATION SYSTEM)
// --------------------------------------------------------------------------
@Composable
fun CadastroClienteScreenContent(
    isEmailTaken: (String) -> Boolean,
    isPhoneTaken: (String) -> Boolean,
    onRegisterSuccess: (User) -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var referralCode by rememberSaveable { mutableStateOf("") }
    
    var acceptTerms by rememberSaveable { mutableStateOf(false) }
    var receivePromo by rememberSaveable { mutableStateOf(true) }
    
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF14532D).copy(alpha = 0.04f),
                radius = 350.dp.toPx(),
                center = Offset(size.width * 0.9f, size.height * 0.1f)
            )
            drawCircle(
                color = Color(0xFFFB923C).copy(alpha = 0.04f),
                radius = 280.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.9f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Criar sua conta",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF14532D)
                    )
                )
                Text(
                    text = "Compre no Bairroo e receba na sua Cidade",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = "" },
                        label = { Text("Nome Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("reg_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = "" },
                        label = { Text("E-mail") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().testTag("reg_email"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; errorMessage = "" },
                        label = { Text("Telefone / WhatsApp") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("reg_phone"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = "" },
                        label = { Text("Criar Senha") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_password"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = "" },
                        label = { Text("Repetir Senha") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { birthDate = it },
                            label = { Text("Data de Nasc.") },
                            placeholder = { Text("DD/MM/AAAA") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = referralCode,
                            onValueChange = { referralCode = it },
                            label = { Text("Cód. Indicação") },
                            placeholder = { Text("Opcional") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = acceptTerms, onCheckedChange = { acceptTerms = it; errorMessage = "" })
                            Text("Li e aceito os Termos de Uso e Política de Privacidade", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = receivePromo, onCheckedChange = { receivePromo = it })
                            Text("Aceito receber promoções no WhatsApp e E-mail", fontSize = 12.sp)
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = Color(0xFFEF4444), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Button(
                        onClick = {
                            val trimmedName = name.trim()
                            val trimmedEmail = email.trim()
                            val trimmedPhone = phone.trim()

                            if (trimmedName.isEmpty() || trimmedEmail.isEmpty() || password.isEmpty()) {
                                errorMessage = "Preencha os campos obrigatórios!"
                                return@Button
                            }
                            if (password != confirmPassword) {
                                errorMessage = "A confirmação de senha não confere!"
                                return@Button
                            }
                            if (!acceptTerms) {
                                errorMessage = "Você precisa aceitar os Termos de Uso!"
                                return@Button
                            }
                            if (isEmailTaken(trimmedEmail)) {
                                errorMessage = "E-mail já está cadastrado no Bairroo!"
                                return@Button
                            }
                            if (isPhoneTaken(trimmedPhone)) {
                                errorMessage = "Telefone celular já cadastrado por outro usuário!"
                                return@Button
                            }

                            val createdUser = User(
                                id = "user_" + System.currentTimeMillis(),
                                email = trimmedEmail,
                                name = trimmedName,
                                role = "client",
                                permissions = listOf("buy", "view_orders"),
                                phone = trimmedPhone,
                                createdAt = "14/06/2026 15:00",
                                active = true,
                                profileComplete = false,
                                favorites = emptyList(),
                                addresses = emptyList(),
                                password = password,
                                roles = listOf("client"),
                                points = if (referralCode.trim().isNotEmpty()) 50 else 0
                            )
                            onRegisterSuccess(createdUser)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("reg_submit"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "FILIAR E MANDAR",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onRegisterSuccess(
                            User(id = "gg_" + System.currentTimeMillis(), email = "google.soc@bairroo.com", name = "Usuário Google", role = "client", active = true, profileComplete = false, roles = listOf("client"))
                        )
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Com Google", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = {
                        onRegisterSuccess(
                            User(id = "ap_" + System.currentTimeMillis(), email = "apple.soc@bairroo.com", name = "Usuário Apple", role = "client", active = true, profileComplete = false, roles = listOf("client"))
                        )
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhoneIphone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Com Apple", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(onClick = onBackToLogin) {
                Text("Já tem uma conta? Voltar ao Login", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF64748B)))
            }
        }
    }
}

// --------------------------------------------------------------------------
// SCREEN: COMPLETAR PERFIL (COMPLETE PROFILE)
// --------------------------------------------------------------------------
@Composable
fun CompleteProfileScreenContent(
    user: User,
    onCompleteProfileSuccess: (User) -> Unit,
    onSkip: () -> Unit
) {
    var avatarSelected by remember { mutableStateOf("🛍️") }
    var cep by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var complement by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Casa") }
    
    var errorMessage by remember { mutableStateOf("") }
    val avatars = listOf("🛍️", "🍔", "🍕", "🎂", "🍎", "🍣", "🚲", "⭐")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF9))
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Completar seu perfil 🚀",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = Color(0xFF14532D))
                )
                Text(
                    text = "Cadastre seu endereço para comprar imediatamente no bairro!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Selecione uma foto/avatar do perfil", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF334155), fontSize = 13.sp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFDCFCE7), CircleShape)
                            .border(2.dp, Color(0xFF14532D), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(avatarSelected, fontSize = 32.sp)
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.height(110.dp).fillMaxWidth(),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(avatars) { av ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(if (avatarSelected == av) Color(0xFFE2E8F0) else Color.Transparent)
                                    .clickable { avatarSelected = av },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(av, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Endereço Principal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = cep,
                        onValueChange = { cep = it; errorMessage = "" },
                        label = { Text("CEP") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it; errorMessage = "" },
                        label = { Text("Endereço (Rua/Av)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = number,
                            onValueChange = { number = it },
                            label = { Text("Número") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = complement,
                            onValueChange = { complement = it },
                            label = { Text("Complemento") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    OutlinedTextField(
                        value = reference,
                        onValueChange = { reference = it },
                        label = { Text("Ponto de Referência") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Text("Salvar como:", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.Gray))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Casa", "Trabalho", "Outro").forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFDCFCE7), selectedLabelColor = Color(0xFF14532D))
                            )
                        }
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    if (cep.isEmpty() || street.isEmpty() || number.isEmpty()) {
                        errorMessage = "CEP, Endereço e Número são obrigatórios."
                        return@Button
                    }
                    val updatedUser = user.copy(
                        profileComplete = true,
                        photo = avatarSelected
                        // Aqui o endereço poderia ser inserido na lista do User, mas por enquanto, atualizamos o status.
                    )
                    onCompleteProfileSuccess(updatedUser)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SALVAR E COMEÇAR A USAR", fontWeight = FontWeight.Black)
            }

            TextButton(onClick = onSkip) {
                Text("Pular por enquanto", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// --------------------------------------------------------------------------
// SCREEN: SEÇÃO BOAS-VINDAS (WELCOME SCREEN)
// --------------------------------------------------------------------------
@Composable
fun WelcomeScreenContent(
    user: User,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFDCFCE7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🎉", fontSize = 56.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Bem-vindo ao Bairroo!",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, color = Color(0xFF14532D)),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Olá, ${user.name}! Descubra as melhores lojas comerciais perto de você e receba suas compras com total segurança na sua Cidade.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(10.dp, RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB923C)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "COMEÇAR A COMPRAR",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                    )
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
