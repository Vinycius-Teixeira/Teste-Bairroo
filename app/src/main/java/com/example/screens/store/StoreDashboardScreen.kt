package com.example.screens.store

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.StoreOrder
import com.example.BairrooLogisticsSettings
import com.example.models.FoodItem
import com.example.*
import kotlinx.coroutines.launch

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodels.OrderViewModel

@Composable
fun StoreDashboardScreen(
    productsList: MutableList<FoodItem>,
    loggedRestaurantId: String,
    orderViewModel: OrderViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = OrderViewModel.Factory),
    onBack: () -> Unit
) {
    val orders by orderViewModel.getLiveOrdersForRestaurant(loggedRestaurantId).collectAsStateWithLifecycle(initialValue = emptyList())
    var currentTab by remember { mutableStateOf("Painel") } // "Painel", "Pedidos", "Produtos", "Mais"
    var activeSubScreen by remember { mutableStateOf<String?>(null) } // null, "MINHA_LOJA", "FINANCEIRO", "AVALIACOES", "NOVO_PRODUTO", "EDITAR_PRODUTO", "PAGAMENTOS_BAIRROO", "VER_PEDIDO", "PROMOCOES_LOJA", "PROMOCOES_PAGAS"
    var selectedProdToEdit by remember { mutableStateOf<FoodItem?>(null) }
    var selectedOrderForSeparation by remember { mutableStateOf<StoreOrder?>(null) }

    // Store settings states (Screen 5)
    var adminMinPrice by remember { mutableStateOf("12.00") }
    var adminPricePerKm by remember { mutableStateOf("1.50") }
    var adminRadiusKm by remember { mutableStateOf("6") }
    var adminBairrooCutPercent by remember { mutableStateOf("15") }

    var storeName by remember { mutableStateOf("Pizza do Bairro") }
    var storeDesc by remember { mutableStateOf("As melhores pizzas da região, feitas com ingredientes selecionados e muito amor!") }
    var storeCat by remember { mutableStateOf("Pizzaria") }
    var storeAddr by remember { mutableStateOf("Rua das Flores, 123 - Centro, São Paulo - SP") }
    var storeFee by remember { mutableStateOf("R$ 5,90") }
    var storeTime by remember { mutableStateOf("35-45 min") }
    var isStoreOpen by remember { mutableStateOf(true) }

    // Módulo 6 custom visual properties
    var storeLogoEmoji by remember { mutableStateOf("🍕") }
    var storeCoverColor by remember { mutableStateOf(Color(0xFFFEF3C7)) } // Light Amber
    var isStoreFollowed by remember { mutableStateOf(false) }

    // Módulo 1 & 3 Commission status
    val context = LocalContext.current
    val commissionRate = remember(storeCat) {
        val rate = getActiveCommissionRateFor(context, storeCat)
        if (rate <= 0f) 8 else rate.toInt()
    }
    var commissionAlertDay by remember { mutableStateOf(5) } // Day 1 = notification, 2-7 = warning, 8+ = lockout
    var isCommissionPaid by remember { mutableStateOf(false) }

    // Módulo 2 Pick checklist
    val pickingChecklist = remember { mutableStateMapOf<String, Boolean>() }
    var showConfirmPickingDialog by remember { mutableStateOf(false) }

    // Módulo 4 & 5 Promotions list and Dialogs
    val storePromotions = remember {
        mutableStateListOf(
            Triple("Combo Família Pizza + Guaraná", 15, "11/07 a 15/07"),
            Triple("Borda Recheada Grátis", 10, "18/07 a 20/07")
        )
    }
    var showCreatePromoDialog by remember { mutableStateOf(false) }
    var newPromoName by remember { mutableStateOf("") }
    var newPromoDisc by remember { mutableStateOf("") }
    var newPromoEndDay by remember { mutableStateOf("15/07") }

    val storePaidCampaigns = remember {
        mutableStateListOf(
            Triple("Banner Destaque - Pizza de Calabresa", "7 dias • Ativo", "Home & Busca")
        )
    }
    var showCreateCampaignDialog by remember { mutableStateOf(false) }
    var campPeriod by remember { mutableStateOf("7 dias") }
    var campCity by remember { mutableStateOf("Divinópolis") }
    var campText by remember { mutableStateOf("") }
    var campDestination by remember { mutableStateOf("Abrir Loja") }

    // Countdown mock state
    var countdownTicks by remember { mutableStateOf(124320) } // Seconds
    LaunchedEffect(key1 = true) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            if (countdownTicks > 0) {
                countdownTicks -= 1
            }
        }
    }
    val displayCountdown = remember(countdownTicks) {
        val days = countdownTicks / (24 * 3600)
        val hours = (countdownTicks % (24 * 3600)) / 3600
        val mins = (countdownTicks % 3600) / 60
        "${String.format("%02d", days)}d ${String.format("%02d", hours)}h ${String.format("%02d", mins)}m"
    }

    // Search and filter states (Screen 2 & 3)
    var orderSearch by remember { mutableStateOf("") }
    var orderPeriod by remember { mutableStateOf("Hoje") }
    var prodSearch by remember { mutableStateOf("") }
    var prodFilterSubTab by remember { mutableStateOf("Todos") } // "Todos", "Ativos", "Inativos"
    val inactiveProds = remember { mutableStateListOf("p4") }

    // Screen 6 product fields
    var pFormName by remember { mutableStateOf("") }
    var pFormDesc by remember { mutableStateOf("") }
    var pFormPrice by remember { mutableStateOf("") }
    var pFormCat by remember { mutableStateOf("Pizzas") }
    var pFormPromoPrice by remember { mutableStateOf("") }
    var pFormStock by remember { mutableStateOf("10") }
    var pFormAvailable by remember { mutableStateOf(true) }

    val mockReviews = remember {
        mutableStateListOf(
            Triple("João da Silva", "15/05/2026", "Pizza sensacional! Chegou quentinha e muito saborosa. ⭐⭐⭐⭐⭐"),
            Triple("Maria Oliveira", "14/05/2026", "Entrega rápida e pizza maravilhosa! ⭐⭐⭐⭐⭐"),
            Triple("Carlos Lima", "13/05/2026", "Muito bom! Só achei que poderia ter mais queijo. ⭐⭐⭐⭐")
        )
    }
    var reviewFilterTab by remember { mutableStateOf("Todas") }

    // Checking Block status (Módulo 1)
    val isCurrentlyBlocked = remember(isCommissionPaid, commissionAlertDay) {
        !isCommissionPaid && commissionAlertDay >= 8
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        if (isCurrentlyBlocked && activeSubScreen != "FINANCEIRO" && activeSubScreen != "PAGAMENTOS_BAIRROO") {
            // Full screen mandatory lockdown (Módulo 1 Alerts)
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(80.dp).background(Color(0xFFFEE2E2), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Lock, "Bloqueado", tint = Color(0xFFEF4444), modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("CONTA BLOQUEADA POR PENDÊNCIA", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFFEF4444)), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Identificamos débitos vencidos de comissões Bairroo há mais de 8 dias (Junho - R$ 340,00). Suas funções de pedidos, produtos e promoções estão suspensas até a regularização do Pix.", style = TextStyle(fontSize = 12.sp, color = Color(0xFF64748B), lineHeight = 16.sp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { activeSubScreen = "PAGAMENTOS_BAIRROO" }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Payment, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Realizar Pagamento (PIX / QR)", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { activeSubScreen = "FINANCEIRO" }, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(12.dp)) {
                        Text("Ver Extrato", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    var showSuporteDialog by remember { mutableStateOf(false) }
                    OutlinedButton(onClick = { showSuporteDialog = true }, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(12.dp)) {
                        Text("Suporte Lojista", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    if (showSuporteDialog) {
                        AlertDialog(
                            onDismissRequest = { showSuporteDialog = false },
                            title = { Text("Suporte Bairroo", fontWeight = FontWeight.Black) },
                            text = { Text("Fale diretamente pelo e-mail suporte@bairroo.com.br ou pelo WhatsApp urgente: (37) 98877-6655. Tempo médio de resposta: 10 minutos.") },
                            confirmButton = { Button(onClick = { showSuporteDialog = false }) { Text("Ok") } }
                        )
                    }
                }
            }
        } else if (activeSubScreen != null) {
            when (activeSubScreen) {
                "MINHA_LOJA" -> {
                    var innerTab by remember { mutableStateOf("Informações") }
                    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { activeSubScreen = null }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                            Text("Minha loja", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF1E293B)))
                        }
                        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F5F9)).padding(8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                            listOf("Informações", "Logística", "Aparência", "Pré-visualização").forEach { t ->
                                val sel = innerTab == t
                                Text(t, modifier = Modifier.clickable { innerTab = t }.padding(vertical = 6.dp, horizontal = 6.dp), style = TextStyle(fontWeight = FontWeight.Bold, color = if (sel) Color(0xFF0F532D) else Color(0xFF64748B), fontSize = 11.sp))
                            }
                        }
                        if (innerTab == "Informações") {
                            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(value = storeName, onValueChange = { storeName = it }, label = { Text("Nome da loja") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = storeDesc, onValueChange = { storeDesc = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = storeCat, onValueChange = { storeCat = it }, label = { Text("Categoria") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = storeAddr, onValueChange = { storeAddr = it }, label = { Text("Endereço") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = storeFee, onValueChange = { storeFee = it }, label = { Text("Taxa de entrega") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = storeTime, onValueChange = { storeTime = it }, label = { Text("Tempo médio") }, modifier = Modifier.fillMaxWidth())
                            }
                        } else if (innerTab == "Logística") {
                            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Parâmetros de Entrega (Administração de Tarifas)", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F532D)))
                                Text("Configure os valores para o cálculo estimado das corridas dos entregadores Bairroo.", fontSize = 11.sp, color = Color(0xFF64748B))
                                
                                OutlinedTextField(value = adminMinPrice, onValueChange = { adminMinPrice = it }, label = { Text("Valor Mínimo de Corrida (R$)") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = adminPricePerKm, onValueChange = { adminPricePerKm = it }, label = { Text("Valor por KM Percorrido (R$)") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = adminRadiusKm, onValueChange = { adminRadiusKm = it }, label = { Text("Raio Máximo de Atendimento (KM)") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = adminBairrooCutPercent, onValueChange = { adminBairrooCutPercent = it }, label = { Text("Participação Bairroo de Retenção (%)") }, modifier = Modifier.fillMaxWidth())
                            }
                        } else if (innerTab == "Aparência") {
                            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Identidade Visual da Loja (Módulo 6)", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp))
                                Text("Escolher Logo Emoji", fontSize = 12.sp, color = Color(0xFF64748B))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf("🍕", "🍔", "🍣", "🍞", "🍦", "🍎").forEach { emoji ->
                                        Box(
                                            modifier = Modifier.size(45.dp).background(if (storeLogoEmoji == emoji) Color(0xFFDCFCE7) else Color(0xFFF1F5F9), RoundedCornerShape(10.dp)).border(2.dp, if (storeLogoEmoji == emoji) Color(0xFF0F532D) else Color.Transparent, RoundedCornerShape(10.dp)).clickable { storeLogoEmoji = emoji },
                                            contentAlignment = Alignment.Center
                                        ) { Text(emoji, fontSize = 24.sp) }
                                    }
                                }
                                Text("Escolher Cor da Capa", fontSize = 12.sp, color = Color(0xFF64748B))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf(Color(0xFFFEF3C7), Color(0xFFFEE2E2), Color(0xFFE0F2FE), Color(0xFFDCFCE7), Color(0xFFF3E8FF)).forEach { clr ->
                                        Box(modifier = Modifier.size(45.dp).background(clr, RoundedCornerShape(10.dp)).border(2.dp, if (storeCoverColor == clr) Color(0xFF0F532D) else Color.Transparent, RoundedCornerShape(10.dp)).clickable { storeCoverColor = clr })
                                    }
                                }
                            }
                        } else {
                            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp), shape = RoundedCornerShape(16.dp)) {
                                    Column {
                                        Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(storeCoverColor), contentAlignment = Alignment.Center) { Text(storeLogoEmoji, fontSize = 48.sp) }
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(storeName, style = TextStyle(fontWeight = FontWeight.Black, fontSize = 18.sp))
                                                    Text(storeCat, style = TextStyle(color = Color(0xFF0F532D), fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                                }
                                                Button(onClick = { isStoreFollowed = !isStoreFollowed }, colors = ButtonDefaults.buttonColors(containerColor = if (isStoreFollowed) Color(0xFF10B981) else Color(0xFF0F532D)), modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 10.dp)) {
                                                    Text(if (isStoreFollowed) "Seguindo ✓" else "Seguir de Graça", fontSize = 10.sp, color = Color.White)
                                                }
                                            }
                                            Text(storeDesc, style = TextStyle(fontSize = 11.sp, color = Color(0xFF64748B)), maxLines = 2)
                                            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Estrelas: ⭐ 4.8", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("Franquia: $storeFee", fontSize = 11.sp, color = Color(0xFF0F532D), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Button(onClick = { activeSubScreen = null }, modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)), shape = RoundedCornerShape(12.dp)) {
                            Text("Salvar e Publicar alterações", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                "FINANCEIRO" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { activeSubScreen = null }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                            Text("Financeiro & Conciliação", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 20.sp))
                        }
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF0F532D)), shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Resumo de Vendas", color = Color.White.copy(alpha = 0.8f))
                                Text("R$ 4.250,00", style = TextStyle(fontWeight = FontWeight.Black, color = Color.White, fontSize = 24.sp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Participação Bairroo: - R$ 340,00 (8%)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text("Líquido: R$ 3.910,00", color = Color(0xFFDCFCE7), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        // NEW DYNAMIC SECTION: CRÉDITO COMPENSAÇÃO (MÓDULO CLIENTE AUSENTE - FINANCEIRO LOJA)
                        val creditsBalance = BairrooLogisticsSettings.merchantCompensationCredit.value
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = BorderStroke(1.5.dp, Color(0xFF10B981))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("💰 SALDO DE COMPENSAÇÃO", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF14532D)))
                                }
                                Text(
                                    text = "R$ ${String.format("%.2f", creditsBalance)}",
                                    style = TextStyle(fontWeight = FontWeight.Black, color = Color(0xFF15803D), fontSize = 22.sp)
                                )
                                Text(
                                    text = "Este saldo compensatório é gerado por reembolsos de multas retidas em cancelamentos de pedidos ausentes (50% do total). É aplicado automaticamente como abatimento na fatura Bairroo.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF374151)
                                )
                            }
                        }

                        // NEW INTERACTIVE SECTION: CALCULADORA DE FECHAMENTO MENSAL COMBINADO OUTLET
                        var showClosureSuccess by remember { mutableStateOf(false) }
                        var baseBill = 340.0
                        var offsetCreditsUsed by remember { mutableStateOf(0.0) }
                        var currentRemainingCredit by remember { mutableStateOf(creditsBalance) }
                        
                        LaunchedEffect(creditsBalance) {
                            currentRemainingCredit = creditsBalance
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("🧮 CALCULADORA DE FECHAMENTO MENSAL", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B)))
                                Text("Arranjo automático entre faturamento Bairroo e créditos compensatórios gerados pelo fluxo de clientes ausentes:", fontSize = 11.sp, color = Color(0xFF475569))
                                
                                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Comissão Base (Junho):", fontSize = 11.sp, color = Color(0xFF334155))
                                            Text("R$ 340,00", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Crédito Compensação Aplicado:", fontSize = 11.sp, color = Color(0xFF334155))
                                            Text("- R$ ${String.format("%.2f", if (showClosureSuccess) offsetCreditsUsed else currentRemainingCredit)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                        }
                                        Divider(color = Color(0xFFE2E8F0))
                                        val netBill = maxOf(0.0, baseBill - (if (showClosureSuccess) offsetCreditsUsed else currentRemainingCredit))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Fatura Líquida Bairroo:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("R$ ${String.format("%.2f", netBill)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                                        }
                                    }
                                }

                                if (showClosureSuccess) {
                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            "✓ Encontro de contas mensal executado! Crédito de R$ ${String.format("%.2f", offsetCreditsUsed)} aplicado com sucesso. Fatura reduzida para R$ ${String.format("%.2f", maxOf(0.0, baseBill - offsetCreditsUsed))}.",
                                            color = Color(0xFF15803D),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            offsetCreditsUsed = currentRemainingCredit
                                            BairrooLogisticsSettings.merchantCompensationCredit.value = 0.0
                                            currentRemainingCredit = 0.0
                                            showClosureSuccess = true
                                            android.widget.Toast.makeText(context, "Fechamento Mensal Calculado e Liquidado!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)),
                                        modifier = Modifier.fillMaxWidth().height(42.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        enabled = currentRemainingCredit > 0.0
                                    ) {
                                        Text(if (currentRemainingCredit > 0.0) "Abater Créditos e Visualizar Fechamento 🧾" else "Nenhum Crédito para Abate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Text("Extrato de lançamentos", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
                        listOf(
                            Triple("Junho/2026", "R$ 4.250,00 (Vendido)", "Comissão: R$ 340,00"),
                            Triple("Maio/2026", "R$ 3.800,00 (Vendido)", "Comissão: R$ 304,00")
                        ).forEach { (dt, raw, detail) ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(dt, fontWeight = FontWeight.Bold)
                                        Text(raw, fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Text(detail, color = Color(0xFF0F532D), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                "PAGAMENTOS_BAIRROO" -> {
                    // Módulo 1: Pagamento de comissão (Bairroo Payments)
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { activeSubScreen = null }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                            Text("Cobranças & Comissão", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 18.sp))
                        }
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Período de Apuração", fontSize = 12.sp, color = Color(0xFF64748B))
                                    Text("Junho/2026", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Volume vendido no mês", fontSize = 12.sp, color = Color(0xFF64748B))
                                    Text("R$ 4.250,00", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Percentual de comissão", fontSize = 12.sp, color = Color(0xFF64748B))
                                    Text("${commissionRate}%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F532D))
                                }
                                Divider(color = Color(0xFFF1F5F9))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Comissão cobrada", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("R$ 340,00", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFEF4444))
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Vencimento", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text("15/06/2026", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Status da Cobrança", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Surface(color = if (isCommissionPaid) Color(0xFFDCFCE7) else Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp)) {
                                        Text(if (isCommissionPaid) "PAGO" else "PENDENTE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = if (isCommissionPaid) Color(0xFF15803D) else Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        if (!isCommissionPaid) {
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), border = BorderStroke(1.dp, Color(0xFFCBD5E1))) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(modifier = Modifier.size(120.dp).background(Color.White).border(1.dp, Color(0xFF94A3B8)), contentAlignment = Alignment.Center) {
                                        Canvas(modifier = Modifier.size(90.dp)) {
                                            val w = size.width
                                            drawRect(color = Color.Black, size = androidx.compose.ui.geometry.Size(25f, 25f))
                                            drawRect(color = Color.Black, size = androidx.compose.ui.geometry.Size(25f, 25f), topLeft = androidx.compose.ui.geometry.Offset(w - 25f, 0f))
                                            drawRect(color = Color.Black, size = androidx.compose.ui.geometry.Size(25f, 25f), topLeft = androidx.compose.ui.geometry.Offset(0f, w - 25f))
                                        }
                                    }
                                    Text("Aponte seu aplicativo do banco para o QR code acima", style = TextStyle(fontSize = 11.sp, color = Color(0xFF64748B)))
                                }
                            }
                            val pixCode = "00020101021226830014br.gov.bcb.pix2561pix.bairroo.com.br/qr/v2/340.00"
                            OutlinedButton(onClick = { android.widget.Toast.makeText(context, "Código PIX copiado!", android.widget.Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp)) {
                                Text("Copiar código PIX Copia e Cola 📋", fontWeight = FontWeight.Bold)
                            }
                            Button(onClick = { isCommissionPaid = true; activeSubScreen = null; android.widget.Toast.makeText(context, "Pagamento feito por Pix! Conta liberada.", android.widget.Toast.LENGTH_LONG).show() }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)), shape = RoundedCornerShape(12.dp)) {
                                Text("Registrar Pagamento (Simular PIX)", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
                "VER_PEDIDO" -> {
                    // Módulo 2: Separar itens do pedido
                    val order = selectedOrderForSeparation
                    if (order != null) {
                        val itemsList = remember(order) {
                            val parts = order.itemsSummary.split("•", ",", "+", " e ").map { it.trim() }.filter { it.isNotEmpty() }
                            if (parts.size >= 2) parts else listOf(order.itemsSummary.ifEmpty { "Pizza Grande Calabresa" }, "Refrigerante 2L", "Borda de Catupiry", "Guardanapos e sachês")
                        }
                        val countChecked = itemsList.count { pickingChecklist[order.id + "_" + it] == true }
                        val finished = countChecked == itemsList.size
                        val progress = if (itemsList.isEmpty()) 0f else countChecked.toFloat() / itemsList.size

                        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { activeSubScreen = null }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                                Text("Conferência & Separação", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 18.sp))
                            }
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Pedido #${order.id}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text("Cliente: ${order.customerName}", fontWeight = FontWeight.Bold)
                                    Text("Status atual: ${order.status}", color = Color(0xFF0F532D), fontWeight = FontWeight.Bold)
                                    Text("Endereço: Avenida Central do Bairro, 1022", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)).clickable { showConfirmPickingDialog = true }.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Marcar todos os itens como prontos", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F532D))
                                Icon(Icons.Default.Check, null, tint = Color(0xFF0F532D))
                            }
                            if (showConfirmPickingDialog) {
                                AlertDialog(
                                    onDismissRequest = { showConfirmPickingDialog = false },
                                    title = { Text("Separar tudo?", fontWeight = FontWeight.Black) },
                                    text = { Text("Confirmar que todos os itens do pedido estão na sacola de envio?") },
                                    confirmButton = { Button(onClick = { itemsList.forEach { pickingChecklist[order.id + "_" + it] = true }; showConfirmPickingDialog = false }) { Text("Sim, Confirmar") } },
                                    dismissButton = { TextButton(onClick = { showConfirmPickingDialog = false }) { Text("Voltar") } }
                                )
                            }
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                itemsList.forEach { item ->
                                    val isChecked = pickingChecklist[order.id + "_" + item] == true
                                    Row(modifier = Modifier.fillMaxWidth().clickable { pickingChecklist[order.id + "_" + item] = !isChecked }.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = isChecked, onCheckedChange = { pickingChecklist[order.id + "_" + item] = it })
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isChecked) "✔ $item (Separado)" else "☐ $item", fontWeight = if (isChecked) FontWeight.Normal else FontWeight.Bold, color = if (isChecked) Color(0xFF94A3B8) else Color(0xFF1E293B))
                                    }
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("$countChecked de ${itemsList.size} separados", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F532D))
                                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = Color(0xFF0F532D), trackColor = Color(0xFFE2E8F0))
                            }

                            if (finished) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("LOGÍSTICA DE ENTREGA 🚀", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF0F532D)))
                                
                                var deliveryMethod by remember(order.id) { mutableStateOf<String?>(null) } // null, "BAIRROO", "PROPRIO"
                                var seekingState by remember(order.id) { mutableStateOf("IDLE") } // "IDLE", "SEEKING", "FOUND", "CONFIRMED"
                                var paymentMethod by remember(order.id) { mutableStateOf("PIX") }
                                var isPaidDirectly by remember(order.id) { mutableStateOf(false) }

                                val estimatedValue = remember(adminMinPrice, adminPricePerKm) {
                                    val base = adminMinPrice.toDoubleOrNull() ?: 12.0
                                    val km = adminPricePerKm.toDoubleOrNull() ?: 1.50
                                    base + (km * 2.0)
                                }

                                if (deliveryMethod == null) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("Como deseja realizar esta entrega?", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 14.sp))
                                            Text("Escolha o método de entrega.", fontSize = 11.sp, color = Color(0xFF64748B))

                                            // Option 1: Bairroo Delivery
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { deliveryMethod = "BAIRROO" },
                                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                                border = BorderStroke(1.5.dp, Color(0xFF0F532D)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text("🛵 Gostaria de um Entregador Bairroo?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F532D))
                                                        Text("Solicite um entregador disponível.", fontSize = 11.sp, color = Color(0xFF64748B))
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                            Text("Entrega: R$ ${String.format("%.2f", estimatedValue)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F532D))
                                                            Text("Tempo: 8–15 min", fontSize = 11.sp, color = Color(0xFF64748B))
                                                        }
                                                    }
                                                    Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF0F532D))
                                                }
                                            }

                                            // Option 2: Own Delivery
                                            OutlinedButton(
                                                onClick = {
                                                    deliveryMethod = "PROPRIO"
                                                    order.status = "Saiu para entrega"
                                                    android.widget.Toast.makeText(context, "Logística própria acionada! Rota iniciada.", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(1.dp, Color(0xFF64748B))
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text("Já tenho entregador", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF334155))
                                                    Text("Usar logística própria. Sem custos Bairroo.", fontSize = 9.sp, color = Color(0xFF64748B))
                                                }
                                            }
                                        }
                                    }
                                } else if (deliveryMethod == "BAIRROO") {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Entregador Bairroo 🛵", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF14532D))
                                                TextButton(onClick = { deliveryMethod = null; seekingState = "IDLE" }) {
                                                    Text("Trocar método", fontSize = 11.sp, color = Color(0xFFEF4444))
                                                }
                                            }

                                            if (seekingState == "IDLE") {
                                                Button(
                                                    onClick = { seekingState = "SEEKING" },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D))
                                                ) {
                                                    Text("Solicitar Entregador Bairroo (R$ ${String.format("%.2f", estimatedValue)})", fontWeight = FontWeight.Bold)
                                                }
                                            } else if (seekingState == "SEEKING") {
                                                LaunchedEffect(key1 = true) {
                                                    kotlinx.coroutines.delay(1500)
                                                    seekingState = "FOUND"
                                                }
                                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF0F532D), strokeWidth = 2.dp)
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text("Buscando entregador mais próximo...", fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 12.sp)
                                                }
                                            } else if (seekingState == "FOUND") {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                            Box(modifier = Modifier.size(45.dp).background(Color(0xFFDCFCE7), CircleShape), contentAlignment = Alignment.Center) {
                                                                Text("🛵", fontSize = 24.sp)
                                                            }
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    Text("Mateus Santana", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                                                        Text("★ 4.9", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                                                    }
                                                                }
                                                                Text("Honda CG 160 Titan Preta • ABC-1234", fontSize = 11.sp, color = Color(0xFF64748B))
                                                                Text("Chega em: 5 min (Estimado)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F532D))
                                                            }
                                                        }
                                                        
                                                        Button(
                                                            onClick = {
                                                                seekingState = "CONFIRMED"
                                                                order.status = "Saiu para entrega"
                                                            },
                                                            modifier = Modifier.fillMaxWidth().height(38.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D))
                                                        ) {
                                                            Text("Confirmar entrega", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            } else if (seekingState == "CONFIRMED") {
                                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("Pedido despachado! Mateus Santana está entregando.", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                                        }
                                                    }

                                                    Text("PAGAMENTO DIRETO", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 11.sp))
                                                    Text("O pagamento da corrida de R$ ${String.format("%.2f", estimatedValue)} deve ser feito diretamente ao entregador. O Bairroo não retém este valor.", fontSize = 11.sp, color = Color(0xFF64748B))

                                                    Text("Método de Pagamento selecionado:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        listOf("PIX", "Dinheiro", "Outro").forEach { method ->
                                                            val active = paymentMethod == method
                                                            OutlinedButton(
                                                                onClick = { paymentMethod = method },
                                                                modifier = Modifier.weight(1f).height(32.dp),
                                                                contentPadding = PaddingValues(1.dp),
                                                                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (active) Color(0xFFE2E8F0) else Color.Transparent),
                                                                border = BorderStroke(1.dp, if (active) Color(0xFF0F532D) else Color(0xFFCBD5E1))
                                                            ) {
                                                                Text(method, fontSize = 9.sp, color = Color.Black, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
                                                            }
                                                        }
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isPaidDirectly = !isPaidDirectly }) {
                                                        Checkbox(checked = isPaidDirectly, onCheckedChange = { isPaidDirectly = it })
                                                        Text("Pagamento realizado diretamente", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }

                                                    Button(
                                                        onClick = {
                                                            order.status = "Entregue"
                                                            activeSubScreen = null
                                                            android.widget.Toast.makeText(context, "Pedido Concluído com Sucesso e Pagamento Confirmado!", android.widget.Toast.LENGTH_SHORT).show()
                                                        },
                                                        enabled = isPaidDirectly,
                                                        modifier = Modifier.fillMaxWidth().height(44.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                                    ) {
                                                        Text("Concluir Pedido (Pagamento Confirmado)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if (deliveryMethod == "PROPRIO") {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("Logística Própria Ativa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("O pedido está com logística própria ativa.", fontSize = 11.sp, color = Color(0xFF64748B))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            
                                            val stepText = when (order.status) {
                                                "Saiu para entrega" -> "Concluir Entrega (Entregue) ✓"
                                                else -> "Marcar Saiu para Entrega 🚚"
                                            }
                                            Button(
                                                onClick = {
                                                    if (order.status == "Saiu para entrega") {
                                                        order.status = "Entregue"
                                                        activeSubScreen = null
                                                    } else {
                                                        order.status = "Saiu para entrega"
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D))
                                            ) {
                                                Text(stepText, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        disabledContainerColor = Color(0xFF94A3B8)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Separe todos para liberar a entrega", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                "PROMOCOES_LOJA" -> {
                    // Módulo 4: Promoções da Loja
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { activeSubScreen = null }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                            Text("Promoções do Cardápio", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 18.sp))
                        }
                        Button(onClick = { showCreatePromoDialog = true }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)), shape = RoundedCornerShape(12.dp)) {
                            Text("＋ Criar Nova Promoção", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        if (showCreatePromoDialog) {
                            AlertDialog(
                                onDismissRequest = { showCreatePromoDialog = false },
                                title = { Text("Configurar Promoção", fontWeight = FontWeight.Black) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(value = newPromoName, onValueChange = { newPromoName = it }, label = { Text("Nome da campanha") })
                                        OutlinedTextField(value = newPromoDisc, onValueChange = { newPromoDisc = it }, label = { Text("Desconto %") })
                                        OutlinedTextField(value = newPromoEndDay, onValueChange = { newPromoEndDay = it }, label = { Text("Término (Ex: 15/07)") })
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        if (newPromoName.isNotEmpty()) {
                                            storePromotions.add(Triple(newPromoName, newPromoDisc.toIntOrNull() ?: 15, "11/07 a $newPromoEndDay"))
                                            newPromoName = ""; newPromoDisc = ""; showCreatePromoDialog = false
                                        }
                                    }) { Text("Criar") }
                                },
                                dismissButton = { TextButton(onClick = { showCreatePromoDialog = false }) { Text("Cancelar") } }
                            )
                        }
                        storePromotions.forEachIndexed { index, promo ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(promo.first, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        Surface(color = Color(0xFFFFEDD5), shape = RoundedCornerShape(8.dp)) {
                                            Text("${promo.second}% OFF", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFFEA580C), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp)).padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Tempo Restante:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                        Text("Termina em: $displayCountdown", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF9A3412))
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(onClick = { newPromoName = promo.first; newPromoDisc = promo.second.toString(); storePromotions.removeAt(index); showCreatePromoDialog = true }, modifier = Modifier.weight(1f).height(38.dp)) { Text("Editar", fontSize = 10.sp) }
                                        OutlinedButton(onClick = { storePromotions.add(Triple(promo.first + " (Cópia)", promo.second, promo.third)) }, modifier = Modifier.weight(1f).height(38.dp)) { Text("Duplicar", fontSize = 10.sp) }
                                        OutlinedButton(onClick = { storePromotions.removeAt(index); android.widget.Toast.makeText(context, "Promoção encerrada, preços originais restaurados.", android.widget.Toast.LENGTH_SHORT).show() }, modifier = Modifier.weight(1f).height(38.dp)) { Text("Encerrar", fontSize = 10.sp) }
                                        IconButton(onClick = { storePromotions.removeAt(index) }, modifier = Modifier.size(38.dp)) { Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444)) }
                                    }
                                }
                            }
                        }
                    }
                }
                "PROMOCOES_PAGAS" -> {
                    // Módulo 5: Promoções Pagas
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { activeSubScreen = null }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                            Text("Destaques Patrocinados", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 18.sp))
                        }
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)), border = BorderStroke(1.dp, Color(0xFFA7F3D0))) {
                            Text("Divulgue sua loja em banners de destaque no topo do Bairroo para conseguir muito mais visitas no cardápio de seu negócio.", modifier = Modifier.padding(12.dp), fontSize = 11.sp, color = Color(0xFF047857))
                        }
                        Button(onClick = { showCreateCampaignDialog = true }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)), shape = RoundedCornerShape(12.dp)) {
                            Text("＋ Contratar Banner Patrocinado", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        if (showCreateCampaignDialog) {
                            AlertDialog(
                                onDismissRequest = { showCreateCampaignDialog = false },
                                title = { Text("Contratar Banner", fontWeight = FontWeight.Black) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(value = campText, onValueChange = { campText = it }, label = { Text("Texto do Banner") })
                                        OutlinedTextField(value = campPeriod, onValueChange = { campPeriod = it }, label = { Text("Duração (Ex: 7 dias)") })
                                        OutlinedTextField(value = campCity, onValueChange = { campCity = it }, label = { Text("Cidade-Sede") })
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        if (campText.isNotEmpty()) {
                                            storePaidCampaigns.add(Triple("Banner: " + campText, "$campPeriod • Ativo", "Home & Categoria"))
                                            campText = ""; showCreateCampaignDialog = false
                                        }
                                    }) { Text("Contratar") }
                                },
                                dismissButton = { TextButton(onClick = { showCreateCampaignDialog = false }) { Text("Voltar") } }
                            )
                        }
                        Text("Seu Banner na Pesquisa do Cliente (Aparência)", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF64748B)))
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF0F532D)), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("💡 PATROCINADO", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                Text("Compre da melhor e mais premiada Pizzaria do Bairro em Divinópolis!", style = TextStyle(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Anunciante Parceiro Bairroo", color = Color(0xFFFCD34D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(storeLogoEmoji, fontSize = 24.sp)
                                }
                            }
                        }
                        Text("Suas campanhas ativas", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp))
                        storePaidCampaigns.forEach { campaign ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(campaign.first, fontWeight = FontWeight.Bold)
                                        Text(campaign.third, fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(8.dp)) { Text(campaign.second, modifier = Modifier.padding(6.dp), color = Color(0xFF065F46), fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
                "AVALIACOES" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { activeSubScreen = null }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                            Text("Avaliações", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 20.sp))
                        }
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text("4,8", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 32.sp))
                                    Text("★ ★ ★ ★ ★", color = Color(0xFFF59E0B), fontSize = 11.sp)
                                    Text("256 avaliações", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }
                                Box(modifier = Modifier.size(1.dp, 60.dp).background(Color(0xFFE2E8F0)))
                                Column(modifier = Modifier.weight(1.2f).padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Excelente: 78%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Muito bom: 15%", fontSize = 11.sp)
                                    Text("Regular: 5%", fontSize = 11.sp)
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Todas", "Positivas", "Negativas").forEach { filter ->
                                val sel = reviewFilterTab == filter
                                Surface(modifier = Modifier.clickable { reviewFilterTab = filter }, color = if (sel) Color(0xFF0F532D) else Color(0xFFF1F5F9), shape = RoundedCornerShape(12.dp)) {
                                    Text(filter, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = if (sel) Color.White else Color(0xFF475569), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                        mockReviews.filter { (_, _, txt) ->
                            when (reviewFilterTab) {
                                "Positivas" -> txt.contains("⭐⭐⭐⭐⭐") || txt.contains("⭐⭐⭐⭐")
                                "Negativas" -> !txt.contains("⭐⭐⭐⭐⭐") && !txt.contains("⭐⭐⭐⭐")
                                else -> true
                            }
                        }.forEach { (author, d, text) ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(author, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(d, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    Text(text, fontSize = 12.sp, color = Color(0xFF475569), modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
                "NOVO_PRODUTO", "EDITAR_PRODUTO" -> {
                    val isEdit = activeSubScreen == "EDITAR_PRODUTO"
                    val prod = selectedProdToEdit
                    LaunchedEffect(prod) {
                        if (isEdit && prod != null) {
                            pFormName = prod.name
                            pFormDesc = prod.description
                            pFormPrice = prod.price.toString()
                            pFormCat = prod.category
                            pFormAvailable = !inactiveProds.contains(prod.id)
                        } else {
                            pFormName = ""
                            pFormDesc = ""
                            pFormPrice = ""
                            pFormCat = "Pizzas"
                            pFormAvailable = true
                        }
                    }
                    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { activeSubScreen = null; selectedProdToEdit = null }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                            Text(if (isEdit) "Editar produto" else "Novo produto", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 20.sp))
                        }
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(value = pFormName, onValueChange = { pFormName = it }, label = { Text("Nome do produto") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = pFormDesc, onValueChange = { pFormDesc = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = pFormCat, onValueChange = { pFormCat = it }, label = { Text("Categoria") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = pFormPrice, onValueChange = { pFormPrice = it }, label = { Text("Preço (R$)") }, modifier = Modifier.fillMaxWidth())
                            
                            // Módulo 3: Transparência da Participação Bairroo (Auto-Calculates Live on Input changes)
                            val parsedDoublePrice = pFormPrice.replace(",", ".").toDoubleOrNull() ?: 0.0
                            val calculatedBPartValue = parsedDoublePrice * (commissionRate / 100.0)
                            val calculatedNetValue = parsedDoublePrice - calculatedBPartValue

                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Participação Bairroo (${commissionRate}%)", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF0F532D), fontSize = 12.sp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Preço Sugerido:", fontSize = 12.sp, color = Color(0xFF475569))
                                        Text("R$ ${String.format("%.2f", parsedDoublePrice)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Valor Pago ao Bairroo:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                        Text("- R$ ${String.format("%.2f", calculatedBPartValue)}", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444), fontSize = 12.sp)
                                    }
                                    Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Receita Líquida (Você recebe):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                        Text("R$ ${String.format("%.2f", calculatedNetValue)}", fontWeight = FontWeight.Black, color = Color(0xFF16A34A), fontSize = 13.sp)
                                    }
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp)).padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Produto disponível", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Switch(checked = pFormAvailable, onCheckedChange = { pFormAvailable = it })
                            }
                        }
                        Button(
                            onClick = {
                                val parsedPrice = pFormPrice.replace(",", ".").toDoubleOrNull() ?: 10.0
                                if (isEdit && prod != null) {
                                    val idx = productsList.indexOfFirst { it.id == prod.id }
                                    if (idx >= 0) {
                                        productsList[idx] = FoodItem(prod.id, pFormName, pFormDesc, parsedPrice, prod.imageType, pFormCat)
                                        if (pFormAvailable) inactiveProds.remove(prod.id) else if (!inactiveProds.contains(prod.id)) inactiveProds.add(prod.id)
                                    }
                                } else if (pFormName.isNotEmpty()) {
                                    val newId = "p" + (productsList.size + 11)
                                    productsList.add(FoodItem(newId, pFormName, pFormDesc, parsedPrice, "pizza", pFormCat))
                                    if (!pFormAvailable) inactiveProds.add(newId)
                                }
                                activeSubScreen = null
                                selectedProdToEdit = null
                            },
                            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Salvar produto", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        } else {
            // Render major tab bars content
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    "Painel" -> {
                        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Módulo 1 Alert Warning banner (days 2 to 7)
                            if (!isCommissionPaid && commissionAlertDay in 2..7) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { activeSubScreen = "PAGAMENTOS_BAIRROO" },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                                    border = BorderStroke(1.dp, Color(0xFFF97316)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, null, tint = Color(0xFFEA580C), modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("PAGAMENTO PENDENTE", fontWeight = FontWeight.Bold, color = Color(0xFFC2410C), fontSize = 12.sp)
                                            Text("A comissão do Junho (R$ 340,00) venceu. Evite bloqueio da conta pagando no Pix.", style = TextStyle(fontSize = 11.sp, color = Color(0xFF9A3412), lineHeight = 14.sp))
                                        }
                                    }
                                }
                            }

                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF0F532D)), shape = RoundedCornerShape(16.dp)) {
                                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text("Olá, $storeName!", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White))
                                        Text(if (isStoreOpen) "Sua loja está aberta para receber pedidos" else "Loja fechada", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    }
                                    Surface(color = if (isStoreOpen) Color(0xFFDCFCE7) else Color(0xFFFEE2E2), shape = RoundedCornerShape(12.dp), modifier = Modifier.clickable { isStoreOpen = !isStoreOpen }) {
                                        Text(if (isStoreOpen) "Aberta 🟢" else "Fechada 🔴", style = TextStyle(fontWeight = FontWeight.Bold, color = if (isStoreOpen) Color(0xFF15803D) else Color(0xFFEF4444), fontSize = 12.sp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }
                            Text("Resumo de hoje", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 16.sp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatsCard(modifier = Modifier.weight(1f), "Vendas", "R$ 1.245,50", "+18,6%")
                                StatsCard(modifier = Modifier.weight(1f), "Pedidos", "32", "+14%")
                            }
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Acompanhamento semanal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                                        val w = size.width
                                        val h = size.height
                                        val pts = listOf(0.2f, 0.45f, 0.35f, 0.75f, 0.60f, 0.95f)
                                        val dx = w / (pts.size - 1)
                                        val path = Path().apply {
                                            moveTo(0f, h - (pts[0] * h))
                                            pts.forEachIndexed { i, p -> if (i > 0) lineTo(i * dx, h - (p * h)) }
                                        }
                                        drawPath(path, Color(0xFF0F532D), style = Stroke(width = 6f))
                                    }
                                }
                            }
                            Text("Pedidos recentes", style = TextStyle(fontWeight = FontWeight.Bold))
                            orders.take(2).forEach { o ->
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(o.customerName, fontWeight = FontWeight.Bold)
                                            Text(o.status, color = Color(0xFF0F532D), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                        Text(o.itemsSummary, fontSize = 11.sp, color = Color(0xFF64748B))
                                        OutlinedButton(
                                            onClick = { selectedOrderForSeparation = o; activeSubScreen = "VER_PEDIDO" },
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, Color(0xFF0F532D))
                                        ) {
                                            Icon(Icons.Default.Checklist, null, modifier = Modifier.size(14.dp), tint = Color(0xFF0F532D))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Conferência & Separação 📦", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F532D))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "Pedidos" -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Pedidos", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 20.sp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Hoje", "Semana", "Mês").forEach { p ->
                                    val sel = orderPeriod == p
                                    Surface(modifier = Modifier.clickable { orderPeriod = p }, color = if (sel) Color(0xFF0F532D) else Color(0xFFF1F5F9), shape = RoundedCornerShape(12.dp)) {
                                        Text(p, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), color = if (sel) Color.White else Color(0xFF475569), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                            OutlinedTextField(value = orderSearch, onValueChange = { orderSearch = it }, placeholder = { Text("Buscar pedidos...") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) })
                            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                val filtered = orders.filter { it.customerName.contains(orderSearch, true) || it.itemsSummary.contains(orderSearch, true) }
                                items(filtered) { o ->
                                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("#${o.id} • ${o.customerName}", fontWeight = FontWeight.Black)
                                                Text(o.status, color = Color(0xFF0F532D), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Text(o.itemsSummary, fontSize = 12.sp, color = Color(0xFF475569))
                                            Text("Total: R$ ${String.format("%.2f", o.totalPrice)}", fontWeight = FontWeight.Bold, color = Color(0xFF14532D), fontSize = 13.sp)
                                            
                                            // Módulo 2 checklist entry button inside card
                                            OutlinedButton(
                                                onClick = { selectedOrderForSeparation = o; activeSubScreen = "VER_PEDIDO" },
                                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFF0F532D))
                                            ) {
                                                Icon(Icons.Default.Checklist, null, modifier = Modifier.size(14.dp), tint = Color(0xFF0F532D))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Acompanhar Pedido 📦", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F532D))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "Produtos" -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Produtos", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 20.sp))
                            OutlinedTextField(value = prodSearch, onValueChange = { prodSearch = it }, placeholder = { Text("Buscar produtos...") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) })
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Todos", "Ativos", "Inativos").forEach { tab ->
                                    val sel = prodFilterSubTab == tab
                                    Surface(modifier = Modifier.clickable { prodFilterSubTab = tab }, color = if (sel) Color(0xFF0F532D) else Color(0xFFF1F5F9), shape = RoundedCornerShape(12.dp)) {
                                        Text(tab, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = if (sel) Color.White else Color(0xFF475569), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val filtered = productsList.filter {
                                    val isInactive = inactiveProds.contains(it.id)
                                    val matchesTab = when (prodFilterSubTab) {
                                        "Ativos" -> !isInactive
                                        "Inativos" -> isInactive
                                        else -> true
                                    }
                                    it.name.contains(prodSearch, true) && matchesTab
                                }
                                items(filtered) { p ->
                                    val isInactive = inactiveProds.contains(p.id)
                                    Card(modifier = Modifier.fillMaxWidth().clickable { selectedProdToEdit = p; activeSubScreen = "EDITAR_PRODUTO" }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Column {
                                                Text(p.name, fontWeight = FontWeight.Bold)
                                                Text("R$ ${String.format("%.2f", p.price)} • ${p.category}", fontSize = 11.sp, color = Color(0xFFFB923C))
                                            }
                                            Surface(color = if (isInactive) Color(0xFFF3F4F6) else Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp)) {
                                                Text(if (isInactive) "Inativo" else "Ativo", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = if (isInactive) Color(0xFF6B7280) else Color(0xFF15803D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                            Button(onClick = { activeSubScreen = "NOVO_PRODUTO" }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)), shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Novo produto", color = Color.White)
                            }
                        }
                    }
                    "Mais" -> {
                        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().clickable { activeSubScreen = "MINHA_LOJA" }, horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(50.dp).background(storeCoverColor, CircleShape), contentAlignment = Alignment.Center) { Text(storeLogoEmoji, fontSize = 24.sp) }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(storeName, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text("Ver minha loja", color = Color(0xFF0F532D), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFCBD5E1))
                            }
                            Divider(color = Color(0xFFF1F5F9))
                            
                            // Debugger controller: Simulating Commission alerts directly in the app UI
                            Text("Demonstração (Alertas de Comissão)", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800), fontSize = 12.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(onClick = { isCommissionPaid = false; commissionAlertDay = 1 }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)), contentPadding = PaddingValues(1.dp)) {
                                    Text("Dia 1 (Aviso)", fontSize = 10.sp, color = Color.Black)
                                }
                                Button(onClick = { isCommissionPaid = false; commissionAlertDay = 5 }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFED7AA)), contentPadding = PaddingValues(1.dp)) {
                                    Text("Dia 2-7 (Banner)", fontSize = 10.sp, color = Color.Black)
                                }
                                Button(onClick = { isCommissionPaid = false; commissionAlertDay = 8 }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)), contentPadding = PaddingValues(1.dp)) {
                                    Text("Dia 8 (Bloqueio)", fontSize = 10.sp, color = Color.Black)
                                }
                            }
                            Divider(color = Color(0xFFF1F5F9))
                            
                            Text("Gerenciar Loja", fontWeight = FontWeight.Bold)
                            MaisOptionRow(Icons.Default.Storefront, "Minha loja (Aparência, Preview)") { activeSubScreen = "MINHA_LOJA" }
                            MaisOptionRow(Icons.Default.Payment, "Pagamentos & Comissão Bairroo") { activeSubScreen = "PAGAMENTOS_BAIRROO" }
                            MaisOptionRow(Icons.Default.LocalOffer, "Campanhas e Promoções do Cardápio") { activeSubScreen = "PROMOCOES_LOJA" }
                            MaisOptionRow(Icons.Default.Campaign, "Banners e Destaques Patrocinados") { activeSubScreen = "PROMOCOES_PAGAS" }
                            MaisOptionRow(Icons.Default.AttachMoney, "Financeiro") { activeSubScreen = "FINANCEIRO" }
                            MaisOptionRow(Icons.Default.Star, "Avaliações") { activeSubScreen = "AVALIACOES" }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFEF4444)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))) {
                                Icon(Icons.Default.Logout, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sair da conta", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Bottom Navigation tabs bar
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, tonalElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 6.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.SpaceAround) {
                    val tabs = listOf(
                        Triple("Painel", Icons.Default.Dashboard, "Painel"),
                        Triple("Pedidos", Icons.Default.ReceiptLong, "Pedidos"),
                        Triple("Produtos", Icons.Default.ShoppingBag, "Produtos"),
                        Triple("Mais", Icons.Default.MoreHoriz, "Mais")
                    )
                    tabs.forEach { (tb, icon, lbl) ->
                        val sel = currentTab == tb
                        Column(modifier = Modifier.weight(1f).clickable { currentTab = tb }, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icon, lbl, tint = if (sel) Color(0xFF0F532D) else Color(0xFF94A3B8), modifier = Modifier.size(24.dp))
                            Text(lbl, fontSize = 11.sp, color = if (sel) Color(0xFF0F532D) else Color(0xFF94A3B8), fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(modifier: Modifier = Modifier, title: String, value: String, trend: String) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title.uppercase(), fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                    Text(trend, color = Color(0xFF15803D), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
            }
        }
    }
}

// --------------------------------------------------------------------------
// SCREEN: ADMIN GLOBAL CONTROL PANEL (ADMIN AREA)
