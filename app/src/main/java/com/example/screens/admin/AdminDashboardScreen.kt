package com.example.screens.admin

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
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.screens.PixAccountManagementScreen
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.models.StoreOrder
import com.example.models.User
import com.example.*
import com.example.CommissionSettingsManager
import com.example.AdminSubScreenHeader
import com.example.CategoryCommissionCard
import com.example.ConfigurationToggleRow
import com.example.AdminKpiSparkCard
import com.example.SalesOverviewChart
import com.example.TopPartnerProgressBarRow
import com.example.TopCategoryBarRow
import com.example.StatusDonutChart
import com.example.DonutLegendRow
import com.example.WarningAlertBox
import com.example.MaisOptionRow
import com.example.CommissionRules
import com.example.PromoCommission
import kotlinx.coroutines.delay
import com.example.viewmodels.AdminDashboardViewModel
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
    usersList: MutableList<User>,
    ordersList: List<StoreOrder>,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminDashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val totalCollected by viewModel.totalCollected.collectAsStateWithLifecycle()
    val totalSpentOnDelivery by viewModel.totalSpentOnDelivery.collectAsStateWithLifecycle()
    
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedAdminTab by remember { mutableStateOf(0) }
    var activeAdminSubScreen by remember { mutableStateOf<String?>(null) }
    
    val usersListState = remember { mutableStateListOf(*usersList.toTypedArray()) }
    var selectedUserToManage by remember { mutableStateOf<User?>(null) }
    var selectedUserNewRole by remember { mutableStateOf("customer") }
    
    var showNotifDropDown by remember { mutableStateOf(false) }
    var reportPeriod by remember { mutableStateOf("01/05/2026 - 31/05/2026") }
    
    val myActiveCoupons = remember { mutableStateListOf(
        Pair("BAIRROO10", "15% OFF"),
        Pair("FRETEGRATIS", "Grátis")
    ) }
    val userReviews = remember { mutableStateListOf(
        Triple("Gustavo Lima", "Pizzaria do Bairro • 5 ★", "Comida excelente e entrega super rápida!"),
        Triple("Camila Queiroz", "Burger House • 4 ★", "Lanche delicioso, mas demorou um pouco.")
    ) }

    // Interactive Admin Order Management States
    var orderSearchQuery by remember { mutableStateOf("") }
    var orderPeriodFilter by remember { mutableStateOf("Hoje") } // "Hoje", "Semana", "Mês"
    val ordersToManage = remember { mutableStateListOf(
        StoreOrder("12548", "Gustavo Lima", "Restaurante do Zé • Coca-Cola", 54.90, "Pendente", "2 min atrás", "Restaurante", null),
        StoreOrder("12547", "Carlos Santana", "Pizzaria do Bairro", 89.90, "Em Preparo", "15 min atrás", "Restaurante", null),
        StoreOrder("12546", "Aline Souza", "Burger House • Batata Frita", 42.50, "Entregue", "1 h atrás", "Restaurante", null),
        StoreOrder("12545", "Luís Ramos", "Sushi House • Combo 30 pçs", 112.00, "Cancelado", "2 h atrás", "Restaurante", null),
        StoreOrder("12544", "Fernanda Costa", "Empório Central • Vinhos", 125.30, "Aceito", "3 h atrás", "Conveniência", null),
        StoreOrder("12543", "Beatriz Rocha", "Açaí da Vila • Copo Grande 500ml", 18.00, "Entregue", "4 h atrás", "Outros", null),
        StoreOrder("12542", "Mariana Lima", "Supermercado Compre Bem • Feira & Mercearia", 150.00, "Entregue", "5 h atrás", "Mercado", null),
        StoreOrder("12541", "Roberto Dias", "Drogaria Central • Medicamentos", 60.00, "Entregue", "6 h atrás", "Farmácia", null),
        StoreOrder("12540", "Tatiane Ramos", "Conveniência Posto Ipiranga • Snacks", 35.00, "Entregue", "7 h atrás", "Conveniência", null)
    ) }

    // Dynamic Report Filtering States
    var reportSelectedType by remember { mutableStateOf("Financeiro") } // "Financeiro", "Pedidos", "Parceiros", "Entregadores", "Assinaturas"
    var reportPeriodFilter by remember { mutableStateOf("Este Mês") } // "Hoje", "Semanal", "Mensal"
    var reportCityFilter by remember { mutableStateOf("Divinópolis") }
    var reportCategoryFilter by remember { mutableStateOf("Todos") }
    
    LaunchedEffect(usersListState.toList()) {
        usersList.clear()
        usersList.addAll(usersListState)
    }
    
    if (activeAdminSubScreen != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAF8))) {
                AdminSubScreenHeader(title = when(activeAdminSubScreen) {
                    "USUARIOS" -> "Usuários"
                    "LOJAS" -> "Lojas & Parceiros"
                    "ENTREGADORES" -> "Logística & Entregas"
                    "FINANCEIRO" -> "Painel Financeiro"
                    "FINANCEIRO_PIX" -> "Central PIX e Recebimentos"
                    "CUPONS" -> "Gerenciador de Cupons"
                    "BAIRROO_MAIS" -> "Bairroo Mais Config"
                    "AVALIACOES" -> "Avaliações Recentes"
                    "CONFIGURACOES" -> "Configurações Gerais"
                    "PERFIL" -> "Meu Perfil"
                    "SEGURANCA" -> "Segurança"
                    "NOTIFICACOES_SETTINGS" -> "Ajustes de Notificação"
                    else -> "Subpágina"
                }.replace("BAIRROO_MAIS", "Bairrooo Mais")) { activeAdminSubScreen = null }
                
                Box(modifier = Modifier.weight(1f)) {
                    when(activeAdminSubScreen) {
                        "FINANCEIRO_PIX" -> PixAccountManagementScreen()
                        "BAIRROO_MAIS" -> {
                             Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                 Text("Gerenciamento do Bairrooo Mais", style = MaterialTheme.typography.titleLarge)
                                 Text("Configurações do plano e parâmetros da fidelidade.")
                             }
                        }
                        "USUARIOS" -> {
                            var searchQuery by remember { mutableStateOf("") }
                            Column(modifier = Modifier.fillMaxSize()) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Pesquisar usuários...") },
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val filtered = usersListState.filter { it.name.contains(searchQuery, ignoreCase = true) }
                                    items(filtered) { user ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            shape = RoundedCornerShape(16.dp),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(user.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937)))
                                                    Text(user.email, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6B7280)))
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(12.dp)) {
                                                        Text(user.role.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp, color = Color(0xFF15803D)))
                                                    }
                                                    IconButton(onClick = {
                                                        selectedUserToManage = user
                                                        selectedUserNewRole = user.role
                                                    }) {
                                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF14532D))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "LOJAS" -> {
                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Monitoramento de Lojas", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                listOf("Pizzaria do Bairro", "Burger House", "Sushi House").forEach { name ->
                                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                            Box(modifier = Modifier.size(10.dp).background(Color(0xFF22C55E), CircleShape))
                                        }
                                    }
                                }
                            }
                        }
                        "ENTREGADORES" -> {
                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Monitoramento de Entregadores", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                listOf("Jefferson Mendes", "Amanda de Jesus").forEach { name ->
                                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                            Box(modifier = Modifier.size(10.dp).background(Color(0xFF22C55E), CircleShape))
                                        }
                                    }
                                }
                            }
                        }
                        "FINANCEIRO" -> {
                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Visão Geral Financeira", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)), modifier = Modifier.weight(1f)) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text("Saldo Entregadores", style = MaterialTheme.typography.labelSmall)
                                                Text("R$ %.2f".format(totalBalance), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFDBEAFE)), modifier = Modifier.weight(1f)) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text("Total Arrecadado", style = MaterialTheme.typography.labelSmall)
                                                Text("R$ %.2f".format(totalCollected), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                    }
                                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("Total Descontado (Entregas)", style = MaterialTheme.typography.labelSmall)
                                            Text("R$ %.2f".format(totalSpentOnDelivery), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                    }
                                }
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text("Gestão das Comissões por Categoria", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                                        Text("Configure diferentes taxas de comissão para Restaurantes, Mercados, Farmácias, Conveniências e outros sem precisar publicar um novo app.", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280)))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = { activeAdminSubScreen = "COMISSOES" }, 
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)), 
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Percent, contentDescription = null, tint = Color.White)
                                                Text("Abrir Configuração de Comissões")
                                            }
                                        }
                                    }

                                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), shape = RoundedCornerShape(12.dp)) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text("Configurações Logísticas (MÓDULO DEVOLUÇÃO)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                                        Text("Configure os tempos de espera dos entregadores, percentuais de multa de cancelamento e valores para segundas tentativas de envios:", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280)))
                                        
                                        var waitState by remember { mutableStateOf(BairrooLogisticsSettings.waitTimeMinutes.value.toString()) }
                                        var fineState by remember { mutableStateOf(BairrooLogisticsSettings.cancellationFinePercent.value.toString()) }
                                        var reducedState by remember { mutableStateOf(BairrooLogisticsSettings.reducedDeliveryFee.value.toString()) }
                                        var regularState by remember { mutableStateOf(BairrooLogisticsSettings.regularDeliveryFee.value.toString()) }
                                        var limitState by remember { mutableStateOf(BairrooLogisticsSettings.reschedulingLimitHours.value.toString()) }

                                        OutlinedTextField(
                                            value = waitState, 
                                            onValueChange = { waitState = it }, 
                                            label = { Text("Tempo Limite de Espera do Cliente (Minutos)") }, 
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = fineState, 
                                            onValueChange = { fineState = it }, 
                                            label = { Text("Multa de Cancelamento Cobrada do Cliente (%)") }, 
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = reducedState, 
                                            onValueChange = { reducedState = it }, 
                                            label = { Text("Taxa para Mesmo Entregador - Reagendamento (R$)") }, 
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = regularState, 
                                            onValueChange = { regularState = it }, 
                                            label = { Text("Taxa para Novo Entregador - Reagendamento (R$)") }, 
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = limitState, 
                                            onValueChange = { limitState = it }, 
                                            label = { Text("Prazo Limite para Reagendamento (Horas)") }, 
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Button(
                                            onClick = { 
                                                BairrooLogisticsSettings.waitTimeMinutes.value = waitState.toIntOrNull() ?: 5
                                                BairrooLogisticsSettings.cancellationFinePercent.value = fineState.toIntOrNull() ?: 20
                                                BairrooLogisticsSettings.reducedDeliveryFee.value = reducedState.toDoubleOrNull() ?: 5.0
                                                BairrooLogisticsSettings.regularDeliveryFee.value = regularState.toDoubleOrNull() ?: 15.0
                                                BairrooLogisticsSettings.reschedulingLimitHours.value = limitState.toIntOrNull() ?: 2
                                                
                                                coroutineScope.launch { snackbarHostState.showSnackbar("Configurações logísticas atualizadas com sucesso!") } 
                                            }, 
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)), 
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Sincronizar Todas as Configurações")
                                        }
                                    }
                                }
                            }
                        }
                        "COMISSOES" -> {
                            val context = LocalContext.current
                            var rulesState by remember { mutableStateOf(CommissionSettingsManager.getRules(context)) }
                            var promoState by remember { mutableStateOf(CommissionSettingsManager.getPromo(context)) }
                            
                            var tempRestaurant by remember { mutableStateOf(rulesState.restaurant) }
                            var tempMarket by remember { mutableStateOf(rulesState.market) }
                            var tempPharmacy by remember { mutableStateOf(rulesState.pharmacy) }
                            var tempConvenience by remember { mutableStateOf(rulesState.convenience) }
                            var tempOthers by remember { mutableStateOf(rulesState.others) }
                            
                            var promoCategorySelected by remember { mutableStateOf("Restaurante") }
                            var promoRateEdit by remember { mutableStateOf(5.0f) }
                            var promoDurationDays by remember { mutableStateOf(30) }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Configuração de Comissões",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                                        )
                                        Text(
                                            text = "Ajuste taxas por categoria sem republicação",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6B7280))
                                        )
                                    }
                                    IconButton(
                                        onClick = { activeAdminSubScreen = "NONE" },
                                        modifier = Modifier.background(Color(0xFFF1F5F9), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                                    }
                                }

                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB))
                                        Column {
                                            Text(
                                                text = "Como funciona?",
                                                style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), fontSize = 12.sp)
                                            )
                                            Text(
                                                text = "Comissões aplicam-se apenas aos novos pedidos. Pedidos passados mantêm a comissão acordada na data de compra.",
                                                style = TextStyle(color = Color(0xFF1E40AF), fontSize = 11.sp)
                                            )
                                        }
                                    }
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF0)),
                                    border = BorderStroke(1.dp, Color(0xFFFFE4CB)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                                                Text(
                                                    "Simulador de Impacto (Real-Time)",
                                                    style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF78350F), fontSize = 14.sp)
                                                )
                                            }
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "PROJEÇÃO",
                                                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309)),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                        
                                        var totalGMV = 0.0
                                        var currentRevenue = 0.0
                                        var simulatedRevenue = 0.0
                                        
                                        ordersToManage.forEach { order ->
                                            if (order.status == "Entregue") {
                                                totalGMV += order.totalPrice
                                                val savedRate = order.commissionRateApplied ?: when(order.category.lowercase()) {
                                                    "restaurante" -> rulesState.restaurant
                                                    "mercado" -> rulesState.market
                                                    "farmácia", "farmacia" -> rulesState.pharmacy
                                                    "conveniência", "conveniencia" -> rulesState.convenience
                                                    else -> rulesState.others
                                                }
                                                currentRevenue += (order.totalPrice * (savedRate / 100.0))
                                                
                                                val simulatedRate = when(order.category.lowercase()) {
                                                    "restaurante" -> tempRestaurant
                                                    "mercado" -> tempMarket
                                                    "farmácia", "farmacia" -> tempPharmacy
                                                    "conveniência", "conveniencia" -> tempConvenience
                                                    else -> tempOthers
                                                }
                                                simulatedRevenue += (order.totalPrice * (simulatedRate / 100.0))
                                            }
                                        }
                                        
                                        val difference = simulatedRevenue - currentRevenue
                                        val differencePct = if (currentRevenue > 0) (difference / currentRevenue) * 100 else 0.0
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("GMV Concluído", style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
                                                Text("R$ %.2f".format(totalGMV), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1F2937)))
                                            }
                                            Column {
                                                Text("Receita Atual", style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
                                                Text("R$ %.2f".format(currentRevenue), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF6B7280)))
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Projeção Simulada", style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
                                                Text("R$ %.2f".format(simulatedRevenue), style = TextStyle(fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF14532D)))
                                            }
                                        }

                                        Divider(color = Color(0xFFFDE8D4))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Variação Projetada", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B5563)))
                                            Surface(
                                                color = if (difference >= 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = if (difference >= 0) "+R$ %.2f (+%.1f%%)".format(difference, differencePct) else "R$ %.2f (%.1f%%)".format(difference, differencePct),
                                                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (difference >= 0) Color(0xFF15803D) else Color(0xFFB91C1C)),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = "Comissão por Categoria",
                                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1F2937))
                                )

                                CategoryCommissionCard(
                                    categoryName = "Restaurante",
                                    icon = "🍔",
                                    currentValue = tempRestaurant,
                                    onValueChange = { tempRestaurant = it },
                                    onSave = {
                                        val newRules = rulesState.copy(restaurant = tempRestaurant, updatedAt = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()))
                                        CommissionSettingsManager.saveRules(context, newRules)
                                        rulesState = newRules
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Taxa de Restaurante salva! Novo valor: ${tempRestaurant}%") }
                                    },
                                    onCancel = { tempRestaurant = rulesState.restaurant },
                                    activeValueDisplay = rulesState.restaurant,
                                    updatedAt = rulesState.updatedAt,
                                    updatedBy = rulesState.updatedBy
                                )

                                CategoryCommissionCard(
                                    categoryName = "Mercado",
                                    icon = "🛒",
                                    currentValue = tempMarket,
                                    onValueChange = { tempMarket = it },
                                    onSave = {
                                        val newRules = rulesState.copy(market = tempMarket, updatedAt = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()))
                                        CommissionSettingsManager.saveRules(context, newRules)
                                        rulesState = newRules
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Taxa de Mercado salva! Novo valor: ${tempMarket}%") }
                                    },
                                    onCancel = { tempMarket = rulesState.market },
                                    activeValueDisplay = rulesState.market,
                                    updatedAt = rulesState.updatedAt,
                                    updatedBy = rulesState.updatedBy
                                )

                                CategoryCommissionCard(
                                    categoryName = "Farmácia",
                                    icon = "💊",
                                    currentValue = tempPharmacy,
                                    onValueChange = { tempPharmacy = it },
                                    onSave = {
                                        val newRules = rulesState.copy(pharmacy = tempPharmacy, updatedAt = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()))
                                        CommissionSettingsManager.saveRules(context, newRules)
                                        rulesState = newRules
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Taxa de Farmácia salva! Novo valor: ${tempPharmacy}%") }
                                    },
                                    onCancel = { tempPharmacy = rulesState.pharmacy },
                                    activeValueDisplay = rulesState.pharmacy,
                                    updatedAt = rulesState.updatedAt,
                                    updatedBy = rulesState.updatedBy
                                )

                                CategoryCommissionCard(
                                    categoryName = "Conveniência",
                                    icon = "🏪",
                                    currentValue = tempConvenience,
                                    onValueChange = { tempConvenience = it },
                                    onSave = {
                                        val newRules = rulesState.copy(convenience = tempConvenience, updatedAt = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()))
                                        CommissionSettingsManager.saveRules(context, newRules)
                                        rulesState = newRules
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Taxa de Conveniência salva! Novo valor: ${tempConvenience}%") }
                                    },
                                    onCancel = { tempConvenience = rulesState.convenience },
                                    activeValueDisplay = rulesState.convenience,
                                    updatedAt = rulesState.updatedAt,
                                    updatedBy = rulesState.updatedBy
                                )

                                CategoryCommissionCard(
                                    categoryName = "Outros",
                                    icon = "📦",
                                    currentValue = tempOthers,
                                    onValueChange = { tempOthers = it },
                                    onSave = {
                                        val newRules = rulesState.copy(others = tempOthers, updatedAt = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()))
                                        CommissionSettingsManager.saveRules(context, newRules)
                                        rulesState = newRules
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Taxa Outros salva! Novo valor: ${tempOthers}%") }
                                    },
                                    onCancel = { tempOthers = rulesState.others },
                                    activeValueDisplay = rulesState.others,
                                    updatedAt = rulesState.updatedAt,
                                    updatedBy = rulesState.updatedBy
                                )

                                Button(
                                    onClick = {
                                        val defaults = CommissionRules()
                                        defaults.updatedAt = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                        defaults.updatedBy = "Silvia (Super Admin)"
                                        CommissionSettingsManager.saveRules(context, defaults)
                                        
                                        tempRestaurant = defaults.restaurant
                                        tempMarket = defaults.market
                                        tempPharmacy = defaults.pharmacy
                                        tempConvenience = defaults.convenience
                                        tempOthers = defaults.others
                                        rulesState = defaults
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Valores padrão restaurados com sucesso!") }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                                        Text("Restaurar comissões padrão", style = TextStyle(fontWeight = FontWeight.Bold))
                                    }
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = if (promoState.isActive) Color(0xFFECFDF5) else Color.White),
                                    border = BorderStroke(1.dp, if (promoState.isActive) Color(0xFFA7F3D0) else Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Icons.Default.Percent, contentDescription = null, tint = if (promoState.isActive) Color(0xFF10B981) else Color(0xFF6B7280))
                                                Text(
                                                    "Modo Promoção",
                                                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                )
                                            }
                                            if (promoState.isActive) {
                                                Surface(
                                                    color = Color(0xFF10B981),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text(
                                                        "ATIVO ⚡",
                                                        style = TextStyle(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 10.sp),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }

                                        if (promoState.isActive) {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = "Promoção ativa para: ${promoState.category}",
                                                    style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                                )
                                                Text(
                                                    text = "Membro Recebe: ${promoState.promoRate}% comissão (estava ${promoState.originalRate}%)",
                                                    style = TextStyle(color = Color(0xFF065F46), fontSize = 13.sp)
                                                )
                                                Text(
                                                    text = "Validade de ${promoState.durationDays} dias (Expira em: ${promoState.expiryDate})",
                                                    style = TextStyle(color = Color(0xFF065F46), fontSize = 12.sp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                OutlinedButton(
                                                    onClick = {
                                                        val canceledPromo = promoState.copy(isActive = false)
                                                        CommissionSettingsManager.savePromo(context, canceledPromo)
                                                        promoState = canceledPromo
                                                        coroutineScope.launch { snackbarHostState.showSnackbar("Campanha promocional encerrada!") }
                                                    },
                                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text("Encerrar Promoção", style = TextStyle(fontWeight = FontWeight.Bold))
                                                }
                                            }
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text(
                                                    text = "Configurar Comissão Temporária",
                                                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                                                )
                                                
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    listOf("Restaurante", "Mercado", "Farmácia", "Conveniência").forEach { cName ->
                                                        val isSel = promoCategorySelected == cName
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .background(
                                                                    if (isSel) Color(0xFF14532D) else Color(0xFFF1F5F9),
                                                                    RoundedCornerShape(8.dp)
                                                                )
                                                                .clickable { promoCategorySelected = cName }
                                                                .padding(vertical = 8.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = cName,
                                                                style = TextStyle(
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (isSel) Color.White else Color(0xFF4B5563)
                                                                )
                                                            )
                                                        }
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("Taxa Promocional", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                                    Text("%.1f %%".format(promoRateEdit), style = TextStyle(fontWeight = FontWeight.Black, color = Color(0xFF14532D)))
                                                }
                                                Slider(
                                                    value = promoRateEdit,
                                                    onValueChange = { promoRateEdit = (Math.round(it * 2) / 2.0f) },
                                                    valueRange = 0f..20f,
                                                    colors = SliderDefaults.colors(
                                                        thumbColor = Color(0xFF14532D),
                                                        activeTrackColor = Color(0xFF14532D)
                                                    )
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("Duração da Promoção", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                                    Text("${promoDurationDays} dias", style = TextStyle(fontWeight = FontWeight.Black))
                                                }
                                                Slider(
                                                    value = promoDurationDays.toFloat(),
                                                    onValueChange = { promoDurationDays = it.toInt() },
                                                    valueRange = 1f..90f,
                                                    colors = SliderDefaults.colors(
                                                        thumbColor = Color(0xFF14532D),
                                                        activeTrackColor = Color(0xFF14532D)
                                                    )
                                                )

                                                Button(
                                                    onClick = {
                                                        val origRate = when(promoCategorySelected.lowercase()) {
                                                            "restaurante" -> rulesState.restaurant
                                                            "mercado" -> rulesState.market
                                                            "farmácia", "farmacia" -> rulesState.pharmacy
                                                            "conveniência", "conveniencia" -> rulesState.convenience
                                                            else -> rulesState.others
                                                        }
                                                        val start = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                                        val cal = java.util.Calendar.getInstance()
                                                        cal.add(java.util.Calendar.DAY_OF_YEAR, promoDurationDays)
                                                        val end = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(cal.time)
                                                        
                                                        val newPromo = PromoCommission(
                                                            category = promoCategorySelected,
                                                            originalRate = origRate,
                                                            promoRate = promoRateEdit,
                                                            durationDays = promoDurationDays,
                                                            isActive = true,
                                                            startDate = start,
                                                            expiryDate = end
                                                        )
                                                        CommissionSettingsManager.savePromo(context, newPromo)
                                                        promoState = newPromo
                                                        coroutineScope.launch { snackbarHostState.showSnackbar("Modo Promoção Ativado: ${promoCategorySelected} em ${promoRateEdit}% por ${promoDurationDays} dias!") }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text("Ativar Modo Promoção", style = TextStyle(fontWeight = FontWeight.Bold))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "CUPONS" -> {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                var code by remember { mutableStateOf("") }
                                var desc by remember { mutableStateOf("") }
                                OutlinedTextField(value = code, onValueChange = { code = it }, placeholder = { Text("Código do Cupom") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = desc, onValueChange = { desc = it }, placeholder = { Text("Desconto") }, modifier = Modifier.fillMaxWidth())
                                Button(onClick = {
                                    if (code.isNotEmpty() && desc.isNotEmpty()) {
                                        myActiveCoupons.add(Pair(code.uppercase(), desc))
                                        code = ""
                                        desc = ""
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Cupom adicionado!") }
                                    }
                                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)), modifier = Modifier.fillMaxWidth()) {
                                    Text("Adicionar Cupom")
                                }
                                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(myActiveCoupons) { coupon ->
                                        Row(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Column {
                                                Text(coupon.first, style = TextStyle(fontWeight = FontWeight.Bold))
                                                Text(coupon.second, style = TextStyle(color = Color(0xFF6B7280)))
                                            }
                                            IconButton(onClick = { myActiveCoupons.remove(coupon) }) {
                                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "BAIRROO_MAIS" -> {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                var price by remember { mutableStateOf("19.90") }
                                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Preço Assinatura Mensal (R$)") }, modifier = Modifier.fillMaxWidth())
                                Button(onClick = { coroutineScope.launch { snackbarHostState.showSnackbar("Bairroo Mais Atualizado!") } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)), modifier = Modifier.fillMaxWidth()) {
                                    Text("Salvar preço")
                                }
                            }
                        }
                        "AVALIACOES" -> {
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(userReviews) { review ->
                                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), shape = RoundedCornerShape(12.dp)) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(review.first, style = TextStyle(fontWeight = FontWeight.Bold))
                                                Text(review.second, style = TextStyle(color = Color(0xFFFB923C)))
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(review.third, style = TextStyle(color = Color(0xFF1F2937)))
                                        }
                                    }
                                }
                            }
                        }
                        "CONFIGURACOES" -> {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                ConfigurationToggleRow(title = "Limitar taxas operacionais", value = true)
                                ConfigurationToggleRow(title = "Auto-faturamento consolidado", value = false)
                                ConfigurationToggleRow(title = "Habilitar criptografia de senhas local", value = true)
                            }
                        }
                        "PERFIL" -> {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Nome: Silvia", style = TextStyle(fontWeight = FontWeight.Bold))
                                Text("E-mail: silvia@bairroo.com.br", style = TextStyle(color = Color(0xFF6B7280)))
                                Text("Acesso: Super Administradora", style = TextStyle(color = Color(0xFF14532D), fontWeight = FontWeight.Bold))
                            }
                        }
                        "SEGURANCA" -> {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                Text("Autenticação em Duas Etapas: HABILITADO ✅", style = TextStyle(fontWeight = FontWeight.Bold))
                            }
                        }
                        "NOTIFICACOES_SETTINGS" -> {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                ConfigurationToggleRow(title = "Push de novos pedidos", value = true)
                                ConfigurationToggleRow(title = "Push de novos parceiros", value = true)
                            }
                        }
                    }
                }
            }
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
        }
    } else {
        Scaffold(
            topBar = {
                Surface(color = Color(0xFF14532D), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White) }
                            Text("Bairroo Admin", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = Color.White))
                        }
                        IconButton(onClick = { showNotifDropDown = !showNotifDropDown }) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White, windowInsets = WindowInsets.navigationBars) {
                    val items = listOf(
                        Triple("Início", Icons.Default.Dashboard, 0),
                        Triple("Pedidos", Icons.Default.ListAlt, 1),
                        Triple("Relatórios", Icons.Default.Assessment, 2),
                        Triple("Mais", Icons.Default.MoreHoriz, 3)
                    )
                    items.forEach { (label, icon, idx) ->
                        NavigationBarItem(
                            selected = selectedAdminTab == idx,
                            onClick = { selectedAdminTab = idx },
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(label, style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF14532D),
                                selectedTextColor = Color(0xFF14532D),
                                indicatorColor = Color(0xFFDCFCE7),
                                unselectedIconColor = Color(0xFF6B7280)
                            )
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color(0xFFFAFAF8)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    when(selectedAdminTab) {
                        0 -> {
                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column {
                                    Text("Olá, Silvia! 👋", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black))
                                    Text("Bem-vinda ao painel administrativo do Bairroo.", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)))
                                }
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth().clickable { coroutineScope.launch { snackbarHostState.showSnackbar("Filtro aplicável nativamente!") } }
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(reportPeriod, style = TextStyle(fontWeight = FontWeight.Bold))
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF14532D))
                                    }
                                }
                                
                                AdminKpiSparkCard(title = "Pedidos no período", value = "1.250", percentText = "↑ 18,6% vs período anterior", points = listOf(0.2f, 0.4f, 0.3f, 0.5f, 0.45f, 0.7f, 0.6f, 0.85f), color = Color(0xFF14532D))
                                AdminKpiSparkCard(title = "Faturamento (GMV)", value = "R$ 72.580,00", percentText = "↑ 22,4% vs período anterior", points = listOf(0.15f, 0.35f, 0.25f, 0.55f, 0.48f, 0.8f, 0.72f, 0.95f), color = Color(0xFF14532D))
                                AdminKpiSparkCard(title = "Receita do Bairroo", value = "R$ 5.846,40", percentText = "↑ 19,8% vs período anterior", points = listOf(0.3f, 0.25f, 0.45f, 0.4f, 0.6f, 0.52f, 0.75f, 0.68f), color = Color(0xFFFB923C))
                                AdminKpiSparkCard(title = "Usuários ativos", value = "3.425", percentText = "↑ 16,2% vs período anterior", points = listOf(0.4f, 0.45f, 0.52f, 0.58f, 0.62f, 0.7f, 0.78f, 0.85f), color = Color(0xFF14532D))
                                
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Visão geral de vendas (Diário)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        SalesOverviewChart(periodType = "Diário", modifier = Modifier.fillMaxWidth().height(150.dp))
                                    }
                                }
                                
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Top parceiros", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                                            Text("Ver todos", color = Color(0xFF14532D), style = TextStyle(fontWeight = FontWeight.Bold), modifier = Modifier.clickable { activeAdminSubScreen = "LOJAS" })
                                        }
                                        TopPartnerProgressBarRow(rank = 1, name = "Pizzaria do Bairro", sales = "R$ 8.760,00")
                                        TopPartnerProgressBarRow(rank = 2, name = "Burger House", sales = "R$ 6.480,00")
                                        TopPartnerProgressBarRow(rank = 3, name = "Sushi House", sales = "R$ 5.230,00")
                                    }
                                }
                                
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text("Top Categorias", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                                        TopCategoryBarRow(name = "Restaurantes", orders = "562 pedidos", pct = 0.45f)
                                        TopCategoryBarRow(name = "Lanches", orders = "320 pedidos", pct = 0.26f)
                                    }
                                }
                                
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text("Métricas de entrega", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Column(modifier = Modifier.weight(1f).background(Color(0xFFFAFAF8), RoundedCornerShape(12.dp)).padding(10.dp)) {
                                                Text("Tempo médio", style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
                                                Text("32 min", style = TextStyle(fontWeight = FontWeight.Bold))
                                            }
                                            Column(modifier = Modifier.weight(1f).background(Color(0xFFFAFAF8), RoundedCornerShape(12.dp)).padding(10.dp)) {
                                                Text("Taxa Conclusao", style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
                                                Text("96,2%", style = TextStyle(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                        Text("Ver painel logístico →", color = Color(0xFF14532D), style = TextStyle(fontWeight = FontWeight.Black), modifier = Modifier.align(Alignment.CenterHorizontally).clickable { activeAdminSubScreen = "ENTREGADORES" })
                                    }
                                }
                            }
                        }
                        1 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Pedidos por status",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                )
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        StatusDonutChart(modifier = Modifier.size(150.dp), totalCount = 1250)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        DonutLegendRow(color = Color(0xFF14532D), label = "Entregues", value = "980 (78%)")
                                        DonutLegendRow(color = Color(0xFF22C55E), label = "Em andamento", value = "150 (12%)")
                                        DonutLegendRow(color = Color(0xFFFB923C), label = "Confirmados", value = "80 (6%)")
                                        DonutLegendRow(color = Color(0xFFEF4444), label = "Cancelados", value = "40 (4%)")
                                    }
                                }

                                // Interactive Pedidos search & list (Visual Screen 2 specs)
                                Text(
                                    text = "Filtro & Busca",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                )
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        // Filter tabs: Hoje, Semana, Mês
                                        Row(
                                            modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAF8), RoundedCornerShape(12.dp)).padding(4.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            listOf("Hoje", "Semana", "Mês").forEach { option ->
                                                val isSelected = orderPeriodFilter == option
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .background(
                                                            if (isSelected) Color(0xFF14532D) else Color.Transparent,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .clickable { orderPeriodFilter = option }
                                                        .padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = option,
                                                        color = if (isSelected) Color.White else Color(0xFF6B7280),
                                                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    )
                                                }
                                            }
                                        }

                                        // Search Bar
                                        OutlinedTextField(
                                            value = orderSearchQuery,
                                            onValueChange = { orderSearchQuery = it },
                                            placeholder = { Text("Pesquisar cliente ou loja...", style = TextStyle(fontSize = 13.sp)) },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6B7280)) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                                focusedBorderColor = Color(0xFF14532D)
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = "Monitoramento de Pedidos",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                )
                                // Lista de pedidos em tempo real (Monitoramento)
                                val filteredOrders = ordersToManage.filter {
                                    it.customerName.contains(orderSearchQuery, ignoreCase = true) ||
                                    it.itemsSummary.contains(orderSearchQuery, ignoreCase = true)
                                }
                                if (filteredOrders.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Nenhum pedido localizado", style = TextStyle(color = Color(0xFF6B7280)))
                                    }
                                } else {
                                    filteredOrders.forEach { order ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = "Pedido #${order.id}",
                                                            style = TextStyle(fontWeight = FontWeight.Black, color = Color(0xFF1F2937), fontSize = 15.sp)
                                                        )
                                                        Text(
                                                            text = order.itemsSummary,
                                                            style = TextStyle(color = Color(0xFF6B7280), fontSize = 12.sp)
                                                        )
                                                    }
                                                    // Status Badges
                                                    val badgeColor = when (order.status) {
                                                        "Pendente" -> Color(0xFFFB923C)
                                                        "Confirmado", "Aceito" -> Color(0xFF3B82F6)
                                                        "Em Preparo", "Em andamento" -> Color(0xFF22C55E)
                                                        "Entregue" -> Color(0xFF14532D)
                                                        else -> Color(0xFFEF4444)
                                                    }
                                                    Surface(
                                                        color = badgeColor.copy(alpha = 0.15f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text(
                                                            text = order.status.uppercase(),
                                                            color = badgeColor,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                                        )
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Cliente: ${order.customerName}",
                                                        style = TextStyle(color = Color(0xFF1F2937), fontSize = 13.sp)
                                                    )
                                                    Text(
                                                        text = "R$ %.2f".format(order.totalPrice),
                                                        style = TextStyle(fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF14532D))
                                                    )
                                                }
                                                
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Tempo: ${order.time}",
                                                        style = TextStyle(color = Color(0xFF6B7280), fontSize = 11.sp)
                                                    )
                                                    // Quick Actions
                                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Recent activities strictly matching mockup Screen 3
                                Text(
                                    text = "Atividades recentes",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                val listActs = listOf(
                                    Triple("Novo pedido #12548", "Restaurante do Zé • R$ 54,90", "2 min"),
                                    Triple("Novo parceiro cadastrado", "Pizzaria do Bairro • Ativo", "15 min"),
                                    Triple("Pagamento recebido", "Repasse Sushi House", "1 h"),
                                    Triple("Novo entregador aprovado", "João da Silva", "2 h"),
                                    Triple("Avaliação recebida", "5 estrelas • Pizzaria do Bairro", "3 h")
                                )
                                listActs.forEach { (tit, desc, ago) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White, RoundedCornerShape(12.dp))
                                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(Color(0xFFFAFAF8), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = when {
                                                        tit.contains("pedido") -> Icons.Default.ReceiptLong
                                                        tit.contains("parceiro") -> Icons.Default.Storefront
                                                        tit.contains("Pagamento") -> Icons.Default.AttachMoney
                                                        tit.contains("entregador") -> Icons.Default.TwoWheeler
                                                        else -> Icons.Default.StarRate
                                                    },
                                                    contentDescription = null,
                                                    tint = Color(0xFF14532D),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Column {
                                                Text(tit, style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937), fontSize = 13.sp))
                                                Text(desc, style = TextStyle(color = Color(0xFF6B7280), fontSize = 12.sp))
                                            }
                                        }
                                        Text(ago, style = TextStyle(color = Color(0xFF6B7280), fontSize = 11.sp))
                                    }
                                }
                            }
                        }
                        2 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Screen 5 Performance cards
                                Text(
                                    text = "Desempenho financeiro",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                )
                                AdminKpiSparkCard(title = "Receita líquida", value = "R$ 5.846,40", percentText = "↑ 19,8% vs mês anterior", points = listOf(0.4f, 0.42f, 0.55f, 0.5f, 0.65f, 0.58f, 0.8f, 0.9f), color = Color(0xFF22C55E))
                                AdminKpiSparkCard(title = "Comissão de lojas", value = "R$ 4.326,80", percentText = "↑ 20,1% vs mês anterior", points = listOf(0.3f, 0.4f, 0.45f, 0.52f, 0.68f, 0.6f, 0.75f, 0.85f), color = Color(0xFF22C55E))
                                AdminKpiSparkCard(title = "Taxa de entrega", value = "R$ 1.234,50", percentText = "↑ 18,3% vs mês anterior", points = listOf(0.2f, 0.35f, 0.41f, 0.38f, 0.58f, 0.52f, 0.7f, 0.75f), color = Color(0xFF22C55E))
                                AdminKpiSparkCard(title = "Assinaturas (Bairroo Mais)", value = "R$ 285,10", percentText = "↑ 25,6% vs mês anterior", points = listOf(0.1f, 0.25f, 0.35f, 0.47f, 0.52f, 0.64f, 0.78f, 0.92f), color = Color(0xFF22C55E))
                                
                                // Screen 5 Alertas e avisos section
                                Text(
                                    text = "Alertas e avisos",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                )
                                WarningAlertBox(badgeText = "15 pedidos cancelados", descText = "Taxa acima do ideal (2,5%)", bgColor = Color(0xFFFEE2E2), badgeColor = Color(0xFFEF4444))
                                WarningAlertBox(badgeText = "3 lojas com problemas", descText = "Verifique as lojas em análise", bgColor = Color(0xFFFEF3C7), badgeColor = Color(0xFFD97706))
                                WarningAlertBox(badgeText = "Tudo em ordem! ✅", descText = "Nenhum alerta ativo", bgColor = Color(0xFFDCFCE7), badgeColor = Color(0xFF15803D))
                                
                                // Screen 5 custom report generator configuration (TELA 3)
                                Text(
                                    text = "Configuração de Relatório",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                )
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                        Text(
                                            "Tipo de Relatório",
                                            style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                        )
                                        
                                        // Dynamic Selection list for Report Category types
                                        val categories = listOf("Financeiro", "Pedidos", "Parceiros", "Entregadores", "Assinaturas")
                                        categories.forEach { cat ->
                                            val isSelected = reportSelectedType == cat
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        if (isSelected) Color(0xFFE8F5E9) else Color.Transparent,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { reportSelectedType = cat }
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = cat,
                                                    style = TextStyle(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) Color(0xFF14532D) else Color(0xFF1F2937)
                                                    )
                                                )
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { reportSelectedType = cat },
                                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF14532D))
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Divider(color = Color(0xFFF1F5F9))

                                        // Filters section: Período, Cidade, Categoria de venda
                                        Text(
                                            "Filtros de Exportação",
                                            style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                        )

                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("Período", style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                listOf("Hoje", "Semana", "Mês").forEach { option ->
                                                    val isSel = reportPeriodFilter == option
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .border(
                                                                1.dp,
                                                                if (isSel) Color(0xFF14532D) else Color(0xFFE2E8F0),
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .background(
                                                                if (isSel) Color(0xFFE8F5E9) else Color.Transparent,
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .clickable { reportPeriodFilter = option }
                                                            .padding(vertical = 8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(option, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color(0xFF14532D) else Color(0xFF1F2937)))
                                                    }
                                                }
                                            }
                                        }

                                        OutlinedTextField(
                                            value = reportCityFilter,
                                            onValueChange = { reportCityFilter = it },
                                            label = { Text("Cidade", style = TextStyle(fontSize = 12.sp)) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        OutlinedTextField(
                                            value = reportCategoryFilter,
                                            onValueChange = { reportCategoryFilter = it },
                                            label = { Text("Categoria de Loja", style = TextStyle(fontSize = 12.sp)) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        var progress by remember { mutableStateOf(0f) }
                                        var generating by remember { mutableStateOf(false) }
                                        var success by remember { mutableStateOf(false) }
                                        
                                        Button(
                                            onClick = {
                                                generating = true
                                                success = false
                                                progress = 0f
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("GERAR RELATÓRIO DO SISTEMA", style = TextStyle(fontWeight = FontWeight.Bold))
                                        }
                                        if (generating) {
                                            LaunchedEffect(Unit) {
                                                for (i in 1..10) {
                                                    delay(120)
                                                    progress = i.toFloat() / 10f
                                                }
                                                generating = false
                                                success = true
                                            }
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                LinearProgressIndicator(progress = progress, color = Color(0xFF14532D), modifier = Modifier.fillMaxWidth())
                                                Text("Processando transações... ${(progress * 100).toInt()}%", style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
                                            }
                                        }
                                        if (success) {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                                                border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text("Exportação Concluída! ✅", color = Color(0xFF15803D), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp))
                                                    Text("Relatório de $reportSelectedType ($reportPeriodFilter) exportado com sucesso para a regional de $reportCityFilter.", color = Color(0xFF166534), style = TextStyle(fontSize = 11.sp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF14532D)).statusBarsPadding().padding(vertical = 24.dp)) {
                                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(modifier = Modifier.size(72.dp).background(Color.White.copy(alpha = 0.2f), CircleShape).border(2.dp, Color(0xFF22C55E), CircleShape), contentAlignment = Alignment.Center) {
                                            Text("S", style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White))
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("Silvia", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                        Text("Super Admin", style = TextStyle(color = Color.White.copy(alpha = 0.8f)))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp), modifier = Modifier.clickable { activeAdminSubScreen = "PERFIL" }) {
                                            Text("Ver perfil", color = Color.White, style = TextStyle(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                                        }
                                    }
                                }
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("Minha Conta", style = TextStyle(color = Color(0xFF6B7280), fontWeight = FontWeight.Bold))
                                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                        Column {
                                            MaisOptionRow(icon = Icons.Default.Person, title = "Perfil") { activeAdminSubScreen = "PERFIL" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.Lock, title = "Segurança") { activeAdminSubScreen = "SEGURANCA" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.Notifications, title = "Notificações") { activeAdminSubScreen = "NOTIFICACOES_SETTINGS" }
                                        }
                                    }
                                    Text("Administração", style = TextStyle(color = Color(0xFF6B7280), fontWeight = FontWeight.Bold))
                                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                        Column {
                                            MaisOptionRow(icon = Icons.Default.People, title = "Usuários") { activeAdminSubScreen = "USUARIOS" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.Storefront, title = "Lojas / Parceiros") { activeAdminSubScreen = "LOJAS" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.TwoWheeler, title = "Entregadores") { activeAdminSubScreen = "ENTREGADORES" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.AttachMoney, title = "Financeiro") { activeAdminSubScreen = "FINANCEIRO" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.AccountBalance, title = "Central PIX") { activeAdminSubScreen = "FINANCEIRO_PIX" }
                                             Divider(color = Color(0xFFF1F5F9))
                                             MaisOptionRow(icon = Icons.Default.TrendingUp, title = "Comissões") { activeAdminSubScreen = "COMISSOES" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.CardGiftcard, title = "Cupons") { activeAdminSubScreen = "CUPONS" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.Star, title = "Bairroo Mais") { activeAdminSubScreen = "BAIRROO_MAIS" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.Star, title = "Avaliações") { activeAdminSubScreen = "AVALIACOES" }
                                            Divider(color = Color(0xFFF1F5F9))
                                            MaisOptionRow(icon = Icons.Default.Settings, title = "Configurações") { activeAdminSubScreen = "CONFIGURACOES" }
                                        }
                                    }
                                    
                                    Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFEF4444)), border = BorderStroke(1.dp, Color(0xFFFCA5A5)), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sair da conta", style = TextStyle(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (showNotifDropDown) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)).clickable { showNotifDropDown = false }, contentAlignment = Alignment.TopCenter) {
                        Card(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable(enabled = false) {}, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Mensagens e Alertas", style = TextStyle(fontWeight = FontWeight.Bold))
                                    TextButton(onClick = { showNotifDropDown = false }) { Text("Fechar") }
                                }
                                Text("• Novo credenciamento enviado por Padaria Real", style = TextStyle(fontSize = 12.sp))
                                Text("• Faturamento semanal atingiu a meta", style = TextStyle(fontSize = 12.sp))
                            }
                        }
                    }
                }
            }
        }
    }
    
    selectedUserToManage?.let { user ->
        AlertDialog(
            onDismissRequest = { selectedUserToManage = null },
            title = { Text("Alterar Perfil de Acesso") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Defina permissão de acesso para ${user.name}:", style = TextStyle(fontSize = 12.sp))
                    listOf("customer", "partner", "driver", "admin").forEach { role ->
                        Row(modifier = Modifier.fillMaxWidth().clickable { selectedUserNewRole = role }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedUserNewRole == role, onClick = { selectedUserNewRole = role })
                            Text(role.uppercase(), modifier = Modifier.padding(start = 8.dp), style = TextStyle(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val idx = usersListState.indexOfFirst { it.id == user.id }
                    if (idx != -1) {
                        usersListState[idx] = user.copy(role = selectedUserNewRole)
                    }
                    selectedUserToManage = null
                    coroutineScope.launch { snackbarHostState.showSnackbar("Cargo atualizado!") }
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D))) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserToManage = null }) { Text("Voltar") }
            }
        )
    }
}
