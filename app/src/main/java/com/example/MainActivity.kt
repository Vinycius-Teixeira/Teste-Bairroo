package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import com.example.screens.store.StoreDetailScreenContent
import com.example.screens.store.StoreWrapperScreen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.models.CancellationRecord
import com.example.models.BairrooAddress
import com.example.models.StoreOrder
import com.example.models.Restaurant
import com.example.data.database.CartItemEntity
import com.example.models.FoodItem
import com.example.models.User
import com.example.components.FoodImagePlaceholder
import com.example.viewmodels.AuthViewModel
import com.example.viewmodels.CartViewModel
import com.example.viewmodels.OrderViewModel
import com.example.viewmodels.AdminDashboardViewModel
import com.example.screens.driver.DriverOrderListScreen
import com.example.screens.driver.DriverDashboardContent
import com.example.screens.admin.AdminDashboardScreen
import com.example.screens.home.HomeScreenContent
import com.example.screens.search.SearchListScreenContent
import com.example.screens.plus.BairrooMaisScreenContent
import com.example.screens.profile.ProfileScreenContent
import com.example.components.BairrooBottomNav
import com.example.screens.partner.SejaParceiroScreenContent
import com.example.screens.store.StoreDashboardScreen
import com.example.screens.auth.LoginScreenContent
import com.example.screens.auth.CadastroClienteScreenContent
import com.example.screens.auth.CompleteProfileScreenContent
import com.example.screens.auth.WelcomeScreenContent
import com.example.screens.PixAccountManagementScreen
import androidx.compose.foundation.text.BasicTextField
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --------------------------------------------------------------------------
// DATA MODELS
// --------------------------------------------------------------------------
data class RestaurantDeprecated(
    val id: String,
    val name: String,
    val rating: Double,
    val category: String,
    val time: String,
    val shipping: String,
    val coupon: String = "",
    val isPartner: Boolean = true,
    val imageType: String = "pizza",
    var score: Int = 100,
    var bairrooMaisParticipation: Boolean = false,
    var bairroooMaisBudget: Double = 0.0,
    var bairroooMaisBudgetUsed: Double = 0.0
)

data class FoodItemDeprecated(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageType: String = "pizza",
    val category: String = "Destaques"
)

enum class ScreenTab {
    HOME, SEARCH, PLUS, PROFILE
}

// [Removed invalid middle import]
// [Removed invalid middle import]

// --------------------------------------------------------------------------
// BALANCED LOGISTICS & CANCELLATION CONFIGURATIONS (ATUALIZAÇÃO BAIRROO)
// --------------------------------------------------------------------------

object BairrooLogisticsSettings {
    var waitTimeMinutes = androidx.compose.runtime.mutableStateOf(5)
    var cancellationFinePercent = androidx.compose.runtime.mutableStateOf(20)
    var reducedDeliveryFee = androidx.compose.runtime.mutableStateOf(5.0)
    var regularDeliveryFee = androidx.compose.runtime.mutableStateOf(15.0)
    var reschedulingLimitHours = androidx.compose.runtime.mutableStateOf(2)
    
    // Merchant Compensation Credit Balance (Credito Compensacao)
    var merchantCompensationCredit = androidx.compose.runtime.mutableStateOf(0.0)
    
    // Bairroo cancellation revenue records
    val cancellationRevenueRecords = androidx.compose.runtime.mutableStateListOf<CancellationRecord>()
}

object BairrooOrderRegistry {
    val partnerOrders = androidx.compose.runtime.mutableStateListOf<StoreOrder>(
        StoreOrder("o1", "Rodrigo Silva", "Vila Burger Clássico + Refrigerante", 32.90, "Em Preparo", "13:40"),
        StoreOrder("o2", "Maria Oliveira", "Vila Burger Bacon", 29.90, "Pendente", "13:45"),
        StoreOrder("o3", "Marcos Souza", "Batata Rústica + Refrigerante", 20.90, "Entregue", "12:15"),
        StoreOrder("o4", "Julia Mendes", "Anéis de Cebola", 16.50, "Enviado", "13:10")
    )
    val customerOrders = androidx.compose.runtime.mutableStateListOf<StoreOrder>(
        StoreOrder("co1", "Rodrigo Silva", "Pizza Calabresa + Coca-Cola 1L", 45.80, "Entregue", "Ontem"),
        StoreOrder("co2", "Rodrigo Silva", "Batata Frita Rustica", 14.90, "Entregue", "10/06/2026")
    )
    
    fun updateStatus(orderId: String, newStatus: String) {
        val pIdx = partnerOrders.indexOfFirst { it.id == orderId }
        if (pIdx != -1) {
            partnerOrders[pIdx] = partnerOrders[pIdx].copy(status = newStatus)
        }
        val cIdx = customerOrders.indexOfFirst { it.id == orderId }
        if (cIdx != -1) {
            customerOrders[cIdx] = customerOrders[cIdx].copy(status = newStatus)
        }
    }
}

data class CommissionRules(
    var restaurant: Float = 8f,
    var market: Float = 6f,
    var pharmacy: Float = 5f,
    var convenience: Float = 8f,
    var others: Float = 8f,
    var updatedAt: String = "14/06/2026 06:14",
    var updatedBy: String = "Silvia (Super Admin)"
)

data class PromoCommission(
    var category: String = "",
    var originalRate: Float = 8f,
    var promoRate: Float = 5f,
    var durationDays: Int = 30,
    var isActive: Boolean = false,
    var startDate: String = "",
    var expiryDate: String = ""
)

object CommissionSettingsManager {
    private const val PREFS_NAME = "BairrooAdminSettings"
    
    fun saveRules(context: android.content.Context, rules: CommissionRules) {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        prefs.edit().apply {
            putFloat("restaurant", rules.restaurant)
            putFloat("market", rules.market)
            putFloat("pharmacy", rules.pharmacy)
            putFloat("convenience", rules.convenience)
            putFloat("others", rules.others)
            putString("updatedAt", rules.updatedAt)
            putString("updatedBy", rules.updatedBy)
            apply()
        }
    }
    
    fun getRules(context: android.content.Context): CommissionRules {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        return CommissionRules(
            restaurant = prefs.getFloat("restaurant", 8f),
            market = prefs.getFloat("market", 6f),
            pharmacy = prefs.getFloat("pharmacy", 5f),
            convenience = prefs.getFloat("convenience", 8f),
            others = prefs.getFloat("others", 8f),
            updatedAt = prefs.getString("updatedAt", "14/06/2026 06:00") ?: "14/06/2026 06:00",
            updatedBy = prefs.getString("updatedBy", "Silvia (Super Admin)") ?: "Silvia (Super Admin)"
        )
    }

    fun savePromo(context: android.content.Context, promo: PromoCommission) {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("promo_category", promo.category)
            putFloat("promo_originalRate", promo.originalRate)
            putFloat("promo_rate", promo.promoRate)
            putInt("promo_duration", promo.durationDays)
            putBoolean("promo_active", promo.isActive)
            putString("promo_start", promo.startDate)
            putString("promo_expiry", promo.expiryDate)
            apply()
        }
    }

    fun getPromo(context: android.content.Context): PromoCommission {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        return PromoCommission(
            category = prefs.getString("promo_category", "") ?: "",
            originalRate = prefs.getFloat("promo_originalRate", 8f),
            promoRate = prefs.getFloat("promo_rate", 5f),
            durationDays = prefs.getInt("promo_duration", 30),
            isActive = prefs.getBoolean("promo_active", false),
            startDate = prefs.getString("promo_start", "") ?: "",
            expiryDate = prefs.getString("promo_expiry", "") ?: ""
        )
    }
}

fun getActiveCommissionRateFor(context: android.content.Context, category: String): Float {
    val rules = CommissionSettingsManager.getRules(context)
    val promo = CommissionSettingsManager.getPromo(context)
    
    if (promo.isActive && promo.category.equals(category, ignoreCase = true)) {
        var isExpired = false
        if (promo.expiryDate.isNotEmpty()) {
            try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                val expiry = sdf.parse(promo.expiryDate)
                val now = java.util.Date()
                if (now.after(expiry)) {
                    isExpired = true
                    val updatedPromo = promo.copy(isActive = false)
                    CommissionSettingsManager.savePromo(context, updatedPromo)
                }
            } catch (e: Exception) {
            }
        }
        if (!isExpired) {
            return promo.promoRate
        }
    }
    
    return when (category.lowercase()) {
        "restaurante" -> rules.restaurant
        "mercado" -> rules.market
        "farmácia", "farmacia" -> rules.pharmacy
        "conveniência", "conveniencia" -> rules.convenience
        else -> rules.others
    }
}

fun mapToCommissionCategory(restaurantCategory: String, restaurantName: String = ""): String {
    val nameLower = restaurantName.lowercase()
    val catLower = restaurantCategory.lowercase()
    
    return when {
        catLower.contains("pizza") || catLower.contains("comida") || catLower.contains("lanches") || catLower.contains("restaurante") || nameLower.contains("pizzaria") || nameLower.contains("burger") || nameLower.contains("sushi") || nameLower.contains("restaurante") -> "Restaurante"
        catLower.contains("mercado") || catLower.contains("supermercado") || nameLower.contains("mercado") || nameLower.contains("compre bem") -> "Mercado"
        catLower.contains("farmácia") || catLower.contains("farmacia") || catLower.contains("drogaria") || nameLower.contains("farmácia") || nameLower.contains("farmacia") || nameLower.contains("drogaria") -> "Farmácia"
        catLower.contains("conveniência") || catLower.contains("conveniencia") || nameLower.contains("conveniência") || nameLower.contains("conveniencia") || nameLower.contains("posto") -> "Conveniência"
        else -> "Outros"
    }
}

// --------------------------------------------------------------------------
// MOCK DATA
// --------------------------------------------------------------------------
val mockRestaurants = listOf(
    Restaurant("1", "Pizzaria do Bairro", 4.8, "Pizza", "30-40 min", "Frete R$ 4,90", "10% OFF", true, "pizza"),
    Restaurant("2", "Sabor Caseiro", 4.7, "Comida Caseira", "25-35 min", "Frete R$ 3,90", "15% OFF", true, "sabor_caseiro"),
    Restaurant("3", "Burger House", 4.6, "Lanches", "20-30 min", "Frete R$ 4,90", "", false, "hamburger"),
    Restaurant("4", "Açaí da Vila", 4.9, "Açaí e Sorvetes", "15-25 min", "Frete R$ 3,50", "", true, "acai")
)

val mockFoodItems = listOf(
    FoodItem("1", "Pizza Marguerita", "Molho de tomate, muçarela, manjericão e azeite.", 38.90, "pizza", "Destaques"),
    FoodItem("2", "Pizza Calabresa", "Molho de tomate, muçarela e calabresa.", 36.90, "pizza", "Destaques"),
    FoodItem("3", "Coca-Cola 1L", "Refrescante embalagem de 1 litro.", 8.90, "coca", "Bebidas"),
    FoodItem("4", "Batata Frita Rustica", "Porção generosa de batata crocante.", 14.90, "sabor_caseiro", "Combos"),
    FoodItem("5", "Combo Burger + Coca-Cola", "Hambúrguer artesanal gourmet com porção.", 32.90, "hamburger", "Combos"),
    FoodItem("6", "Suco de Laranja 500ml", "Suco natural espremido na hora.", 9.90, "sabor_caseiro", "Bebidas")
)

// --------------------------------------------------------------------------
// MAIN ACTIVITY
// --------------------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bairroo force reload activation
        enableEdgeToEdge()
        setContent {
            var darkThemeManual by rememberSaveable { mutableStateOf(false) }
            MyApplicationTheme(darkTheme = darkThemeManual) {
                BairrooApp(
                    darkTheme = darkThemeManual,
                    onToggleTheme = { darkThemeManual = !darkThemeManual }
                )
            }
        }
    }
}

// Retain old Greeting helper for Robolectric screenshot safety test
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

// --------------------------------------------------------------------------
// MAIN APP SCREEN COMPOSABLE
// --------------------------------------------------------------------------
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BairrooApp(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    // --------------------------------------------------------------------------
    // DEV FLAG PARA TESTES RÁPIDOS
    // --------------------------------------------------------------------------
    val DEV_MODE = true

    // --------------------------------------------------------------------------
    // AUTHENTICATION & GLOBAL EXPERIENCES STATE
    // --------------------------------------------------------------------------
    var isLogged by rememberSaveable { mutableStateOf(DEV_MODE) }
    var loggedUserId by rememberSaveable { mutableStateOf(if (DEV_MODE) "2" else "") }
    var loggedUserEmail by rememberSaveable { mutableStateOf("") }
    var loggedUserName by rememberSaveable { mutableStateOf("") }
    var loggedUserRole by rememberSaveable { mutableStateOf(if (DEV_MODE) "partner" else "customer") } // "customer", "partner", "driver", "admin"
    
    // Sub-experience overlay panel: "NONE", "ADMIN_PANEL", "STORE_PANEL", "DRIVER_PANEL"
    var currentSubExperience by rememberSaveable { mutableStateOf(if (DEV_MODE) "STORE_PANEL" else "NONE") }

    // New persistent fields and structures
    var authScreen by rememberSaveable { mutableStateOf("LOGIN") } // "LOGIN", "REGISTER", "COMPLETE_PROFILE", "WELCOME"
    var tempRegisteredUser by remember { mutableStateOf<User?>(null) }
    var loggedUserRef by remember { mutableStateOf<User?>(null) }
    
    var showProfileSelectorDialog by remember { mutableStateOf(false) }
    var userForProfileSelection by remember { mutableStateOf<User?>(null) }
    
    val authViewModel: AuthViewModel = viewModel()
    val addresses by authViewModel.addresses.collectAsStateWithLifecycle()

    // User Directory mutable state (accessible and editable in real-time by Admin!)
    val usersList = remember {
        mutableStateListOf(
            User(
                id = "1", 
                email = "cliente@bairroo.com", 
                name = "Rodrigo Silva", 
                role = "client", 
                permissions = listOf("buy", "view_orders"),
                phone = "(37) 98877-1234",
                createdAt = "14/06/2026 12:00",
                active = true,
                profileComplete = true,
                addresses = listOf(BairrooAddress("add1", "Casa", "35500-120", "Rua das Conchas", "102", "Bloco C", "Próximo ao mercadinho", true)),
                roles = listOf("client")
            ),
            User(
                id = "2", 
                email = "parceiro@bairroo.com", 
                name = "Vila Burger", 
                role = "partner", 
                permissions = listOf("buy", "view_orders", "manage_store"),
                phone = "(37) 99911-2233",
                createdAt = "14/06/2026 12:00",
                active = true,
                profileComplete = true,
                roles = listOf("client", "partner")
            ),
            User(
                id = "3", 
                email = "entregador@bairroo.com", 
                name = "João Entregador", 
                role = "driver", 
                permissions = listOf("buy", "view_orders", "deliver"),
                phone = "(37) 99988-7766",
                createdAt = "14/06/2026 12:00",
                active = true,
                profileComplete = true,
                roles = listOf("client", "driver")
            ),
            User(
                id = "4", 
                email = "admin@bairroo.com", 
                name = "Silvia Super Admin", 
                role = "admin", 
                permissions = listOf("buy", "view_orders", "bairroo_mais", "admin_dashboard"),
                phone = "(37) 99955-4433",
                createdAt = "14/06/2026 12:00",
                active = true,
                profileComplete = true,
                roles = listOf("client", "admin")
            )
        )
    }

    // Set proper user ref on state change
    LaunchedEffect(isLogged, loggedUserId) {
        if (isLogged) {
            val matched = usersList.find { it.id == loggedUserId }
            if (matched != null) {
                loggedUserRef = matched
            }
        } else {
            loggedUserRef = null
        }
    }

    // Dynamic product state list for Partner Shop
    val partnerProductsState = remember {
        mutableStateListOf(
            FoodItem("p1", "Vila Burger Clássico", "Hambúrguer artesanal 150g, muçarela, alface, tomate e maionese.", 26.90, "hamburger", "Destaques"),
            FoodItem("p2", "Vila Burger Bacon", "Hambúrguer artesanal 150g, muçarela, bacon crocante e maionese.", 29.90, "hamburger", "Destaques"),
            FoodItem("p3", "Batata Rústica", "Batatas fritas rústicas salpicadas de páprica e alecrim.", 14.90, "sabor_caseiro", "Acompanhamentos"),
            FoodItem("p4", "Refrigerante Lata", "Refrigerante 350ml geladinho e refrescante.", 6.00, "coca", "Bebidas"),
            FoodItem("p5", "Anéis de Cebola", "Anéis de cebola empanados ultra-crocantes.", 16.50, "hamburger", "Acompanhamentos")
        )
    }

    // Dynamic orders state list for Partner Shop & Admin Panel
    val partnerOrdersState = remember { BairrooOrderRegistry.partnerOrders }

    // Customer past orders simulator state (Rodrigo's checkout orders)
    val customerOrdersState = remember { BairrooOrderRegistry.customerOrders }

    var activeTab by rememberSaveable { mutableStateOf(ScreenTab.HOME) }
    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }
    var isCheckout by remember { mutableStateOf(false) }
    
    // Cart management STATE
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModel.Factory)
    val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    
    // Order management STATE
    val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory)
    
    // Bairroo Mais Points State
    var userPlusPoints by rememberSaveable { mutableStateOf(2250) }
    
    // Seja Parceiro Forms States (Only for mock customer onboarding if they want to sign up!)
    var currentPartnerChoice by rememberSaveable { mutableStateOf("NONE") } // "NONE", "STORE", "DRIVER"
    var storeFormStep by rememberSaveable { mutableStateOf(1) } // 1 to 5
    var driverFormStep by rememberSaveable { mutableStateOf(1) } // 1 to 5
    
    // Store variables
    var companyName by rememberSaveable { mutableStateOf("") }
    var companyCategory by rememberSaveable { mutableStateOf("Mercado") }
    var responsibleName by rememberSaveable { mutableStateOf("") }
    var companyCnpj by rememberSaveable { mutableStateOf("") }
    var companyPhone by rememberSaveable { mutableStateOf("") }
    var storeDocumentUploaded by rememberSaveable { mutableStateOf(false) }
    var storeUploadingDoc by rememberSaveable { mutableStateOf(false) }

    // Driver variables
    var selectedVehicle by rememberSaveable { mutableStateOf("Moto") }
    var driverBrandModel by rememberSaveable { mutableStateOf("") }
    var driverPlate by rememberSaveable { mutableStateOf("") }
    var driverDocumentUploaded by rememberSaveable { mutableStateOf(false) }
    var driverUploadingDoc by rememberSaveable { mutableStateOf(false) }

    // Driver Dashboard Panel states
    var isDriverOnline by rememberSaveable { mutableStateOf(false) }
    var dailyEarnings by rememberSaveable { mutableStateOf(156.80) }
    var dailyDeliveriesCount by rememberSaveable { mutableStateOf(8) }
    var activeDeliveryOffer by rememberSaveable { mutableStateOf(false) }
    var deliveryProgressState by rememberSaveable { mutableStateOf("NONE") } // "NONE", "ACCEPTED", "PICKUP", "DELIVERING", "COMPLETED"
    var vehicleProgress by rememberSaveable { mutableStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Logic: If driver turns online, trigger delivery request mockup after delay
    LaunchedEffect(isDriverOnline) {
        if (isDriverOnline) {
            delay(3000)
            activeDeliveryOffer = true
        } else {
            activeDeliveryOffer = false
        }
    }

    // Coroutine to animate route progress when driver accepts offer
    fun startDeliveryProcess() {
        coroutineScope.launch {
            activeDeliveryOffer = false
            deliveryProgressState = "ACCEPTED"
            vehicleProgress = 0f
            
            // Go to Restaurant Pickup
            delay(1500)
            deliveryProgressState = "PICKUP"
            
            // Deliver route animation
            deliveryProgressState = "DELIVERING"
            val animationSteps = 15
            for (i in 1..animationSteps) {
                delay(120)
                vehicleProgress = i.toFloat() / animationSteps
            }
            
            // Arrived & Complete
            deliveryProgressState = "COMPLETED"
            delay(1000)
            
            // Add earnings and count
            dailyEarnings += 8.50
            dailyDeliveriesCount += 1
            deliveryProgressState = "NONE"
            vehicleProgress = 0f
            
            snackbarHostState.showSnackbar(
                message = "Corrida concluída! + R$ 8,50 creditados.",
                withDismissAction = true
            )
            
            // Offer next delivery
            delay(3000)
            if (isDriverOnline) {
                activeDeliveryOffer = true
            }
        }
    }

    // --------------------------------------------------------------------------
    // RENDER CONTROLLER (MIDDLEWARE)
    // --------------------------------------------------------------------------
    val performLogUserIn: (User, String) -> Unit = { user, chosenRole ->
        loggedUserId = user.id
        loggedUserEmail = user.email
        loggedUserName = user.name
        loggedUserRole = if (chosenRole == "client" || chosenRole == "customer") "customer" else chosenRole // Map "client" to "customer" for back-compatibility
        isLogged = true
        activeTab = ScreenTab.HOME
        currentSubExperience = if (chosenRole == "partner") "STORE_PANEL" else if (chosenRole == "driver") "DRIVER_PANEL" else "NONE"
        authScreen = "LOGIN"
        showProfileSelectorDialog = false
    }

    val handleLoginSuccess: (User) -> Unit = { user ->
        if (user.roles.size > 1) {
            userForProfileSelection = user
            showProfileSelectorDialog = true
        } else {
            performLogUserIn(user, user.role)
        }
    }

    if (!isLogged) {
        if (showProfileSelectorDialog && userForProfileSelection != null) {
            ProfileSelectorCard(
                user = userForProfileSelection!!,
                onRoleSelected = { role: String ->
                    performLogUserIn(userForProfileSelection!!, role)
                },
                onCancel = {
                    showProfileSelectorDialog = false
                    userForProfileSelection = null
                }
            )
        } else {
            when (authScreen) {
                "LOGIN" -> {
                    var loginError by remember { mutableStateOf<String?>(null) }
                    val authViewModel = remember { AuthViewModel() }
                    LoginScreenContent(
                        errorMessage = loginError,
                        onLoginClick = { email, password ->
                            val error = authViewModel.login(email, password, usersList)
                            if (error != null) {
                                loginError = error
                            } else {
                                val user = usersList.find { it.email.equals(email, ignoreCase = true) }
                                if (user != null) handleLoginSuccess(user)
                            }
                        },
                        onCreateAccountClick = {
                            authScreen = "REGISTER"
                        },
                        onClearError = {
                            loginError = null
                        }
                    )
                }
                "REGISTER" -> {
                    CadastroClienteScreenContent(
                        isEmailTaken = { email -> usersList.any { it.email.equals(email, ignoreCase = true) } },
                        isPhoneTaken = { phone -> usersList.any { it.phone == phone && phone.isNotEmpty() } },
                        onRegisterSuccess = { newUser ->
                            // Temporarily hold registered user
                            usersList.add(newUser)
                            tempRegisteredUser = newUser
                            authScreen = "COMPLETE_PROFILE"
                        },
                        onBackToLogin = {
                            authScreen = "LOGIN"
                        }
                    )
                }
                "COMPLETE_PROFILE" -> {
                    if (tempRegisteredUser != null) {
                        CompleteProfileScreenContent(
                            user = tempRegisteredUser!!,
                            onCompleteProfileSuccess = { completedUser ->
                                // Update user database entry
                                val idx = usersList.indexOfFirst { it.id == completedUser.id }
                                if (idx != -1) {
                                    usersList[idx] = completedUser
                                }
                                tempRegisteredUser = completedUser
                                authScreen = "WELCOME"
                            },
                            onSkip = {
                                val completedUser = tempRegisteredUser!!.copy(profileComplete = true)
                                val idx = usersList.indexOfFirst { it.id == completedUser.id }
                                if (idx != -1) {
                                    usersList[idx] = completedUser
                                }
                                tempRegisteredUser = completedUser
                                authScreen = "WELCOME"
                            }
                        )
                    } else {
                        authScreen = "LOGIN"
                    }
                }
                "WELCOME" -> {
                    if (tempRegisteredUser != null) {
                        WelcomeScreenContent(
                            user = tempRegisteredUser!!,
                            onStart = {
                                performLogUserIn(tempRegisteredUser!!, "client")
                                tempRegisteredUser = null
                            }
                        )
                    } else {
                        authScreen = "LOGIN"
                    }
                }
            }
        }
    } else {
        // Render sub-experience overlay panels (Internal Modules)
        if (currentSubExperience == "ADMIN_PANEL" && loggedUserRole == "admin") {
            AdminDashboardScreen(
                usersList = usersList,
                ordersList = partnerOrdersState,
                onBack = { currentSubExperience = "NONE" },
                onLogout = {
                    // Fully log out
                    isLogged = false
                    loggedUserId = ""
                    loggedUserEmail = ""
                    loggedUserName = ""
                    loggedUserRole = "customer"
                    currentSubExperience = "NONE"
                    activeTab = ScreenTab.HOME
                }
            )
        } else if (currentSubExperience == "STORE_PANEL" && loggedUserRole == "partner") {
            StoreDashboardScreen(
                productsList = partnerProductsState,
                loggedRestaurantId = loggedUserId,
                onBack = { currentSubExperience = "NONE" }
            )
        } else if (currentSubExperience == "DRIVER_PANEL" && loggedUserRole == "driver") {
            BairrooDriverPanelScreen(
                onBack = { currentSubExperience = "NONE" }
            )
        } else {
            // Render normal client app featuring selected active tab
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    BairrooBottomNav(
                        activeTab = activeTab,
                        onTabSelect = { tab ->
                            activeTab = tab
                            // Reset inner stack on tab click
                            if (tab != ScreenTab.SEARCH) {
                                selectedRestaurant = null
                            }
                        }
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    modifier = Modifier.padding(innerPadding)
                ) { targetTab ->
                    when (targetTab) {
                        ScreenTab.HOME -> {
                            HomeScreenContent(
                                darkTheme = darkTheme,
                                onToggleTheme = onToggleTheme,
                                onNavigateToSearch = { activeTab = ScreenTab.SEARCH },
                                onNavigateToPlus = { activeTab = ScreenTab.PLUS },
                                onNavigateToPartner = {
                                    // Gated redirect: If they are already a partner, skip SignUp and go directly to Store Dashboard!
                                    if (loggedUserRole == "partner") {
                                        currentSubExperience = "STORE_PANEL"
                                    } else {
                                        // Standard onboarding wizard for candidates
                                        // Quick set role and simulate
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Para testar o fluxo de parceiro, altere seu cargo no Painel Admin ou clique em pre-filled!")
                                        }
                                    }
                                    activeTab = ScreenTab.PROFILE
                                },
                                onSelectRestaurant = { rest ->
                                    selectedRestaurant = rest
                                    activeTab = ScreenTab.SEARCH
                                },
                                currentUser = loggedUserRef,
                                addresses = addresses,
                                onAddAddress = { activeTab = ScreenTab.PROFILE },
                                onShowSnackbar = { msg ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                        ScreenTab.SEARCH -> {
                            if (selectedRestaurant == null) {
                                SearchListScreenContent(
                                    onSelectRestaurant = { rest -> selectedRestaurant = rest }
                                )
                            } else if (isCheckout) {
                                CheckoutReviewScreenContent(
                                    restaurant = selectedRestaurant!!,
                                    cartItems = cartItems,
                                    addresses = addresses,
                                    onBackPress = { isCheckout = false },
                                    onConfirm = {
                                        coroutineScope.launch {
                                            val orderTotalPrice = cartItems.sumOf { it.price * it.quantity }
                                            val itemsSummary = cartItems.joinToString(", ") { "${it.quantity}x ${it.name}" }
                                            
                                            val novaOrdem = com.example.models.StoreOrder(
                                                id = java.util.UUID.randomUUID().toString(),
                                                customerId = loggedUserId,
                                                customerName = loggedUserName.ifEmpty { "Cliente Bairroo" },
                                                restaurantId = selectedRestaurant!!.id,
                                                totalPrice = orderTotalPrice,
                                                status = "Pendente",
                                                itemsSummary = itemsSummary,
                                                time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                                                timestamp = System.currentTimeMillis()
                                            )
                                            
                                            orderViewModel.createOrder(novaOrdem)
                                            
                                            cartViewModel.clearCart()
                                            snackbarHostState.showSnackbar("Pedido finalizado com sucesso!")
                                            isCheckout = false
                                            selectedRestaurant = null
                                        }
                                    },
                                    onRemoveItem = { id -> cartViewModel.removeItem(id) }
                                )
                            } else {
                                com.example.screens.store.StoreDetailScreenContent(
                                    restaurant = selectedRestaurant!!,
                                    cartItems = cartItems,
                                    onAddToCart = { item -> cartViewModel.addItem(item) },
                                    onRemoveFromCart = { itemId -> cartViewModel.removeItem(itemId) },
                                    onCheckoutClick = { isCheckout = true },
                                    onBackPress = { selectedRestaurant = null }
                                )
                            }
                        }
                        ScreenTab.PLUS -> {
                            BairrooMaisScreenContent(
                                points = userPlusPoints,
                                onRedeemCoupon = { cost, successMsg ->
                                    if (userPlusPoints >= cost) {
                                        userPlusPoints -= cost
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(successMsg)
                                        }
                                        true
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Pontos insuficientes para resgate!")
                                        }
                                        false
                                    }
                                }
                            )
                        }
                        ScreenTab.PROFILE -> {
                            ProfileScreenContent(
                                currentRole = loggedUserRole,
                                userName = loggedUserName,
                                userEmail = loggedUserEmail,
                                customerOrders = customerOrdersState,
                                onOpenExperience = { exp -> currentSubExperience = exp },
                                onLogoutClick = {
                                    // Reset authentication
                                    isLogged = false
                                    loggedUserId = ""
                                    loggedUserEmail = ""
                                    loggedUserName = ""
                                    loggedUserRole = "customer"
                                    currentSubExperience = "NONE"
                                    activeTab = ScreenTab.HOME
                                },
                                usersList = usersList,
                                loggedUserId = loggedUserId,
                                onRoleChange = { newRole ->
                                    val mapped = if (newRole == "client" || newRole == "customer") "customer" else newRole
                                    loggedUserRole = mapped
                                    // Auto-boot subexperience based on role selection
                                    when (mapped) {
                                        "partner" -> currentSubExperience = "STORE_PANEL"
                                        "driver" -> currentSubExperience = "DRIVER_PANEL"
                                        else -> currentSubExperience = "NONE"
                                    }
                                },
                                onUserUpdated = { updatedUser ->
                                    val idx = usersList.indexOfFirst { it.id == updatedUser.id }
                                    if (idx != -1) {
                                        usersList[idx] = updatedUser
                                        // Update local states too
                                        loggedUserName = updatedUser.name
                                        loggedUserEmail = updatedUser.email
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------
// BOTTOM NAVIGATION (4 TABS)
// --------------------------------------------------------------------------

// --------------------------------------------------------------------------
// SHARED VISUAL UTILS
// --------------------------------------------------------------------------
@Composable
fun BairrooLogo(
    fontSize: TextUnit = 30.sp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Bairr",
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = (-1).sp
            )
        )
        Text(
            text = "oo",
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                color = Color(0xFF22C55E),
                letterSpacing = (-1).sp
            )
        )
        Spacer(modifier = Modifier.width(2.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFFFB923C), CircleShape)
                .align(Alignment.Bottom)
                .padding(bottom = 6.dp)
        )
    }
}

// --------------------------------------------------------------------------
// SCREEN 1: HOME CONTENT
// --------------------------------------------------------------------------

// --------------------------------------------------------------------------
// SCREEN 2: SEARCH LIST SCREEN CONTENT
// --------------------------------------------------------------------------

// Helper composables that were likely lost
// Removed duplicate FoodImagePlaceholder


@Composable
fun FoodMenuItemCard(foodItem: Any, quantity: Int, onAdd: () -> Unit, onSubtract: () -> Unit) {
    // Simple placeholder implementation
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text("Item")
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onSubtract) { Text("-") }
            Text("$quantity")
            Button(onClick = onAdd) { Text("+") }
        }
    }
}

@Composable
fun BoxScope.CustomAlignBottomCenter(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.align(Alignment.BottomCenter), content = { content() })
}



// --------------------------------------------------------------------------
// SCREEN 3: BAIRROO MAIS PREMIUM AREA
// --------------------------------------------------------------------------

// --------------------------------------------------------------------------
// SCREEN 4: SEJA PARCEIRO WIZARD
// --------------------------------------------------------------------------


// --------------------------------------------------------------------------
// SCREEN RESOLUTION ADAPTIVE FRAME PADDING UTILS
// --------------------------------------------------------------------------
@Composable
fun screenFramePadding(): PaddingValues {
    val screenWidth = LocalContext.current.resources.configuration.screenWidthDp
    return if (screenWidth > 600) {
        PaddingValues(horizontal = 48.dp, vertical = 24.dp)
    } else {
        PaddingValues(16.dp)
    }
}

// --------------------------------------------------------------------------
// PIXEL-PERFECT VECTOR ILLUSTRATIONS (GEOMETRIC CUSTOM CANVAS SHAPES)
// --------------------------------------------------------------------------

// --------------------------------------------------------------------------
// COMPONENTE: PROFILE SELECTOR DIALOG (SMART LOGIN)
// --------------------------------------------------------------------------
@Composable
fun ProfileSelectorCard(
    user: User,
    onRoleSelected: (String) -> Unit,
    onCancel: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                "Acessar qual perfil?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = Color(0xFF14532D))
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                    "Esta conta ${user.name} possui múltiplos perfis ativos. Escolha com qual deseja operar no momento:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B))
                )
                
                user.roles.forEach { role ->
                    val (label, desc, icon, color) = when(role) {
                        "client", "customer" -> Quadruple("Cliente / Comprador", "Pesquise lojas e compre na sua Cidade", Icons.Default.Person, Color(0xFF14532D))
                        "partner" -> Quadruple("Parceiro Lojista", "Gerencie seu estoque e pedidos da loja", Icons.Default.Storefront, Color(0xFFC2410C))
                        "driver" -> Quadruple("Entregador", "Visualize rotas e realize entregas expressas", Icons.Default.TwoWheeler, Color(0xFF1D4ED8))
                        else -> Quadruple(role.uppercase(), "Acessar visualização operacional", Icons.Default.Shield, Color(0xFF475569))
                    }
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRoleSelected(role) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.06f)),
                        border = BorderStroke(1.5.dp, color.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(color, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(label, style = TextStyle(fontWeight = FontWeight.Bold, color = color, fontSize = 13.sp))
                                Text(desc, style = TextStyle(color = Color(0xFF64748B), fontSize = 10.sp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text("CANCELAR", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF64748B)))
            }
        }
    )
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// --------------------------------------------------------------------------
// SCREEN: PROFILE TAB (ROLE SEGREGATION MANAGER)
// --------------------------------------------------------------------------

// --------------------------------------------------------------------------

@Composable
fun AdminSubScreenHeader(title: String, onBack: () -> Unit) {
    Surface(color = Color(0xFF14532D), modifier = Modifier.fillMaxWidth().shadow(2.dp)) {
        Row(
            modifier = Modifier.statusBarsPadding().padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White) }
            Text(title, style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Black))
        }
    }
}

@Composable
fun AdminMiniStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
            Text(value, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color))
        }
    }
}

@Composable
fun AdminKpiSparkCard(title: String, value: String, percentText: String, points: List<Float>, color: Color, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), shape = RoundedCornerShape(16.dp), modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(title, style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
                Text(value, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1F2937)))
                Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Text(percentText, style = TextStyle(fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
            SparklineGraph(points = points, color = color, modifier = Modifier.weight(0.8f).height(44.dp).padding(start = 12.dp))
        }
    }
}

@Composable
fun SparklineGraph(points: List<Float>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (points.isNotEmpty()) {
            val w = size.width
            val h = size.height
            val step = w / (points.size - 1)
            val path = Path().apply {
                moveTo(0f, h - (points[0] * h))
                points.forEachIndexed { i, p -> if (i > 0) lineTo(i * step, h - (p * h)) }
            }
            drawPath(path = path, color = color, style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            val area = Path().apply {
                addPath(path)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(area, Brush.verticalGradient(listOf(color.copy(alpha = 0.15f), Color.Transparent)))
        }
    }
}

@Composable
fun SalesOverviewChart(periodType: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        for (i in 0..3) {
            val y = (h / 3) * i
            drawLine(Color(0xFFF1F5F9), start = Offset(0f, y), end = Offset(w, y), strokeWidth = 1f)
        }
        val points = listOf(0.4f, 0.55f, 0.35f, 0.65f, 0.45f, 0.75f, 0.9f)
        val stepX = w / (points.size - 1)
        val path = Path().apply {
            moveTo(0f, h - (points[0] * (h - 20f)))
            points.forEachIndexed { i, v -> if (i > 0) lineTo(i * stepX, h - (v * (h - 20f))) }
        }
        drawPath(path, Color(0xFF14532D), style = Stroke(6f, join = StrokeJoin.Round))
    }
}

@Composable
fun StatusDonutChart(modifier: Modifier = Modifier, totalCount: Int = 1250) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 24f
            val sMin = size.minDimension - stroke
            val tLeft = Offset((size.width - sMin) / 2, (size.height - sMin) / 2)
            val rSz = Size(sMin, sMin)
            drawArc(Color(0xFF14532D), 0f, 280f, false, topLeft = tLeft, size = rSz, style = Stroke(stroke))
            drawArc(Color(0xFF22C55E), 280f, 40f, false, topLeft = tLeft, size = rSz, style = Stroke(stroke))
            drawArc(Color(0xFFFB923C), 320f, 25f, false, topLeft = tLeft, size = rSz, style = Stroke(stroke))
            drawArc(Color(0xFFEF4444), 345f, 15f, false, topLeft = tLeft, size = rSz, style = Stroke(stroke))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$totalCount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
            Text("Total", style = TextStyle(fontSize = 11.sp, color = Color(0xFF6B7280)))
        }
    }
}

@Composable
fun DonutLegendRow(color: Color, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Text(label, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold))
        }
        Text(value, style = TextStyle(fontSize = 12.sp, color = Color(0xFF6B7280)))
    }
}

@Composable
fun TopPartnerProgressBarRow(rank: Int, name: String, sales: String) {
    val pct = when(rank) {
        1 -> 1f
        2 -> 0.75f
        else -> 0.6f
    }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("#$rank", style = TextStyle(fontWeight = FontWeight.Black, color = Color(0xFF6B7280)), modifier = Modifier.width(28.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = TextStyle(fontWeight = FontWeight.Bold))
            Box(modifier = Modifier.fillMaxWidth(0.9f).height(6.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(3.dp))) {
                Box(modifier = Modifier.fillMaxWidth(pct).height(6.dp).background(Color(0xFF14532D), RoundedCornerShape(3.dp)))
            }
        }
        Text(sales, style = TextStyle(fontWeight = FontWeight.Black))
    }
}

@Composable
fun TopCategoryBarRow(name: String, orders: String, pct: Float) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1.2f)) {
            Text(name, style = TextStyle(fontWeight = FontWeight.Bold))
            Text(orders, style = TextStyle(color = Color(0xFF6B7280), fontSize = 11.sp))
        }
        Box(modifier = Modifier.weight(1f).height(6.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(3.dp))) {
            Box(modifier = Modifier.fillMaxWidth(pct).height(6.dp).background(Color(0xFF22C55E), RoundedCornerShape(3.dp)))
        }
    }
}

@Composable
fun WarningAlertBox(badgeText: String, descText: String, bgColor: Color, badgeColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().background(bgColor, RoundedCornerShape(10.dp)).padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(badgeText, style = TextStyle(fontWeight = FontWeight.Bold, color = badgeColor))
            Text(descText, style = TextStyle(color = Color(0xFF1F2937).copy(alpha = 0.8f), fontSize = 11.sp))
        }
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = badgeColor, modifier = Modifier.size(14.dp))
    }
}

@Composable
fun MaisOptionRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
            Text(title, style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937)))
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFE2E8F0))
    }
}

@Composable
fun ConfigurationToggleRow(title: String, value: Boolean) {
    var state by remember { mutableStateOf(value) }
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF334155)))
        Switch(checked = state, onCheckedChange = { state = it })
    }
}

@Composable
fun CategoryCommissionCard(
    categoryName: String,
    icon: String,
    currentValue: Float,
    onValueChange: (Float) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    activeValueDisplay: Float,
    updatedAt: String,
    updatedBy: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = icon, style = TextStyle(fontSize = 20.sp))
                    Text(
                        text = categoryName,
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1F2937))
                    )
                }
                
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Ativo: %.1f %%".format(activeValueDisplay),
                        style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 11.sp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ajustar comissão:",
                    style = TextStyle(fontSize = 13.sp, color = Color(0xFF4B5563))
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BasicTextField(
                        value = "%.1f".format(currentValue),
                        onValueChange = { inputStr ->
                            val cleanInput = inputStr.replace(",", ".").toFloatOrNull()
                            if (cleanInput != null && cleanInput in 0f..30f) {
                                onValueChange(cleanInput)
                            }
                        },
                        textStyle = TextStyle(fontWeight = FontWeight.Black, color = Color(0xFF14532D), fontSize = 15.sp, textAlign = TextAlign.End),
                        modifier = Modifier
                            .width(50.dp)
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                    Text(text = "%", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937)))
                }
            }

            Slider(
                value = currentValue,
                onValueChange = { onValueChange((Math.round(it * 2) / 2.0f)) },
                valueRange = 0f..30f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF14532D),
                    activeTrackColor = Color(0xFF14532D)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Última alteração: $updatedAt",
                    style = TextStyle(fontSize = 11.sp, color = Color(0xFF9CA3AF))
                )
                Text(
                    text = "Por: $updatedBy",
                    style = TextStyle(fontSize = 11.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
                )
            }

            Divider(color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Color(0xFF94A3B8))
                ) {
                    Text("Cancelar", style = TextStyle(color = Color(0xFF475569)))
                }
                
                val hasChanges = currentValue != activeValueDisplay
                Button(
                    onClick = onSave,
                    enabled = hasChanges,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF14532D),
                        disabledContainerColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Salvar", style = TextStyle(color = Color.White))
                }
            }
        }
    }
}

// --------------------------------------------------------------------------
// DEDICATED MODEL FOR COURIER LOGISTICS
// --------------------------------------------------------------------------
data class MockRide(
    val id: String,
    val storeName: String,
    val distance: String,
    val price: Double,
    val timeToArrive: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val itemsSummary: String,
    val observations: String
)

// --------------------------------------------------------------------------
// SCREEN: PAINEL COMPLETO DO ENTREGADOR (MÓDULO 2 & 3)
// --------------------------------------------------------------------------
@Composable
fun BairrooDriverPanelScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var activeTab by rememberSaveable { mutableStateOf("Início") } // "Início", "Corridas", "Ganhos", "Pedidos", "Mais"
    val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory)
    val driverId = "driver123" // Assuming a fixed driverId for now
    
    // Online state
    var isOnline by rememberSaveable { mutableStateOf(true) }
    
    // Simulation state for Bairroo Debt
    var debtBalance by rememberSaveable { mutableStateOf(120.0) } // Starts at R$120.00
    
    // Completed metrics
    var totalRidesCount by rememberSaveable { mutableStateOf(300) }
    var todayRidesCount by rememberSaveable { mutableStateOf(6) }
    var todayEarnings by rememberSaveable { mutableStateOf(120.0) }
    
    // Available Offer simulation state
    var hasOffer by rememberSaveable { mutableStateOf(true) }
    var offerCountdown by rememberSaveable { mutableStateOf(20) }
    
    // Active delivery details
    var activeRideDetail by remember { mutableStateOf<MockRide?>(null) }
    var activeRideStatus by rememberSaveable { mutableStateOf("NONE") } // "NONE", "ACCEPTED", "ARRIVED", "PICKED_UP", "DELIVERING", "COMPLETED"
    
    // New states for MÓDULO CLIENTE AUSENTE
    var arrivedAtDestinationTime by remember { mutableStateOf<String?>(null) }
    var arrivedAtDestinationLoc by remember { mutableStateOf<String?>(null) }
    var isWaitingSimulated by remember { mutableStateOf(false) }
    var waitTimeLeftSeconds by remember { mutableStateOf(300) } // Default 5 mins
    val notificationLogs = remember { mutableStateListOf<String>() }
    var isReturningToStore by remember { mutableStateOf(false) }
    
    // Vehicle edit states
    var vehicleType by rememberSaveable { mutableStateOf("Moto 🛵") } // "Moto 🛵", "Carro 🚗", "Bike 🚲"
    var vehiclePlate by rememberSaveable { mutableStateOf("ABC-1234") }
    var vehicleModel by rememberSaveable { mutableStateOf("Honda CG 160 Titan Preta") }
    
    // PIX repayment simulator
    var paymentConfirmedByDriver by rememberSaveable { mutableStateOf(false) }

    // Wait timer simulation countdown
    LaunchedEffect(isWaitingSimulated, waitTimeLeftSeconds) {
        if (isWaitingSimulated && waitTimeLeftSeconds > 0) {
            kotlinx.coroutines.delay(1000)
            waitTimeLeftSeconds--
            
            // Generate visual notifications in real-time
            when (waitTimeLeftSeconds) {
                290 -> if (!notificationLogs.contains("📱 Notificação enviada: 'Entregador chegou ao endereço!'")) {
                    notificationLogs.add("📱 Notificação enviada: 'Entregador chegou ao endereço!'")
                }
                250 -> if (!notificationLogs.contains("📞 Chamada efetuada via aplicativo Bairroo. Sem resposta.")) {
                    notificationLogs.add("📞 Chamada efetuada via aplicativo Bairroo. Sem resposta.")
                }
                210 -> if (!notificationLogs.contains("🔔 Alerta de Interfone ativado para apartamento do cliente.")) {
                    notificationLogs.add("🔔 Alerta de Interfone ativado para apartamento do cliente.")
                }
                150 -> if (!notificationLogs.contains("💬 SMS enviado: 'Pedido retornará ao lojista se ausente'.")) {
                    notificationLogs.add("💬 SMS enviado: 'Pedido retornará ao lojista se ausente'.")
                }
            }
        }
    }

    // Run countdown timer for the rides
    LaunchedEffect(hasOffer, offerCountdown) {
        if (hasOffer && offerCountdown > 0) {
            kotlinx.coroutines.delay(1000)
            offerCountdown--
        } else if (offerCountdown == 0) {
            hasOffer = false
        }
    }

    // Debt warning logic according to specifications
    val overLimit200 = debtBalance >= 200.0 && debtBalance < 400.0
    val overBlock400 = debtBalance >= 400.0

    Scaffold(
        bottomBar = {
            // Navigation with safe navigationBar padding or window insets padding
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // If debt exceeds R$400, "Início/Corridas" are blocked, but Finance (Ganhos), Perfil/Mais, and Payments (under Ganhos/repayment) are allowed!
                NavigationBarItem(
                    selected = activeTab == "Início",
                    enabled = !overBlock400,
                    onClick = { activeTab = "Início" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == "Corridas",
                    enabled = !overBlock400,
                    onClick = { activeTab = "Corridas" },
                    icon = { Icon(Icons.Default.DirectionsBike, contentDescription = "Corridas") },
                    label = { Text("Corridas", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == "Ganhos",
                    onClick = { activeTab = "Ganhos" },
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Ganhos") },
                    label = { Text("Ganhos", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == "Pedidos",
                    onClick = { activeTab = "Pedidos" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Pedidos") },
                    label = { Text("Pedidos", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == "Mais",
                    onClick = { activeTab = "Mais" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Mais") },
                    label = { Text("Mais", fontSize = 11.sp) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (overBlock400) Color(0xFF1E293B) else Color(0xFF0F532D),
                tonalElevation = 4.dp
            ) {
                // ... (Header implementation) ...
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                                Text("🛵", fontSize = 20.sp)
                            }
                            Column {
                                Text("ÁREA DO ENTREGADOR", style = TextStyle(fontWeight = FontWeight.Black, color = Color.White, fontSize = 14.sp, letterSpacing = 1.sp))
                                Text(if (isOnline) "🟢 ONLINE & RECEBENDO" else "⚪ OFFLINE", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White)
                        }
                    }
                }
            }

            // Debt Notification Banner (orange) - Day 2-7 or approaching limit
            if (overLimit200 && !overBlock400) {
                Surface(
                    color = Color(0xFFFF9800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Atenção: Seu repasse pendente (R$ ${String.format("%.2f", debtBalance)}) está próximo do limite de R$ 200,00! Evite bloqueios.",
                            style = TextStyle(fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Critical Blocked Modal / View (if over R$ 400 debt block)
            if (overBlock400 && (activeTab == "Início" || activeTab == "Corridas")) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🛑", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("CONTA BLOQUEADA", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Red))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Seu repasse pendente atingiu R$ ${String.format("%.2f", debtBalance)}, superando o limite máximo permitido de R$ 400,00.",
                        style = TextStyle(fontSize = 14.sp, color = Color(0xFF475569)),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Regularize pendências financeiras via PIX na aba 'Ganhos' para liberar o recebimento de corridas.",
                        style = TextStyle(fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { activeTab = "Ganhos" },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D))
                    ) {
                        Text("Ir para Financeiro & Pagar PIX", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Regular tabs content
                when (activeTab) {
                    "Início" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Online Offline Switch Row
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Ficar Online", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("Fique ativo para receber corridas de lojas locais", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Switch(
                                        checked = isOnline,
                                        onCheckedChange = { isOnline = it }
                                    )
                                }
                            }

                            // Simulation slider card to let reviewers test the debt boundaries easily!
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("🎛️ SIMULADOR DE COBRANÇAS & DÍVIDA", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFB45309)))
                                    Text("Simule de forma interativa a dívida de comissão do entregador para testar avisos ou bloqueios do Bairroo:", fontSize = 10.sp, color = Color(0xFF78350F))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { debtBalance = 50.0 },
                                            modifier = Modifier.weight(1f).height(30.dp),
                                            contentPadding = PaddingValues(1.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) { Text("R$ 50 (Ok)", fontSize = 10.sp, color = Color.White) }
                                        Button(
                                            onClick = { debtBalance = 240.0 },
                                            modifier = Modifier.weight(1f).height(30.dp),
                                            contentPadding = PaddingValues(1.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                                        ) { Text("R$ 240 (Alerta)", fontSize = 10.sp, color = Color.White) }
                                        Button(
                                            onClick = { debtBalance = 420.0 },
                                            modifier = Modifier.weight(1f).height(30.dp),
                                            contentPadding = PaddingValues(1.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                                        ) { Text("R$ 420 (Bloq)", fontSize = 10.sp, color = Color.White) }
                                    }
                                }
                            }

                            // Earnings Summary Cards Rows
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Card(modifier = Modifier.weight(1f).height(100.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                        Text("Ganhos Hoje", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text("R$ ${String.format("%.2f", todayEarnings)}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF0F532D))
                                        Text("Direto da loja", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                                Card(modifier = Modifier.weight(1f).height(100.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                        Text("Corridas Hoje", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text("$todayRidesCount Corridas", fontWeight = FontWeight.Black, fontSize = 18.sp)
                                        Text("Meta Dia: 10", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Card(modifier = Modifier.weight(1f).height(90.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Tempo de Giro", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text("5h online", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(progress = 0.5f, modifier = Modifier.fillMaxWidth(), color = Color(0xFF10B981))
                                    }
                                }
                                Card(modifier = Modifier.weight(1f).height(90.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Taxa de Aceite", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text("96.8%", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF10B981))
                                        Text("Excepcional", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                            }

                            // Driver available rides stream
                            Text("CORRIDAS DISPONÍVEIS 🎯", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF1E293B)))

                            if (!isOnline) {
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("⚪", fontSize = 32.sp)
                                        Text("Você está offline", fontWeight = FontWeight.Bold)
                                        Text("Mude o interruptor acima para ficar online e receber as corridas das lojas.", fontSize = 11.sp, color = Color(0xFF64748B), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                    }
                                }
                            } else if (activeRideDetail != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                    border = BorderStroke(1.dp, Color(0xFF10B981))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("⚡ Você tem uma corrida em andamento!", fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                        Text("Consulte e atualize seu progresso na aba 'Corridas'.", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Button(
                                            onClick = { activeTab = "Corridas" },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Ver Corrida Ativa 📦", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else if (!hasOffer) {
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("🛰️", fontSize = 32.sp)
                                        Text("Aguardando nova corrida...", fontWeight = FontWeight.Bold)
                                        Text("Posicionando-se perto de lojas acelera buscas.", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Button(
                                            onClick = {
                                                hasOffer = true
                                                offerCountdown = 20
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D))
                                        ) {
                                            Text("Gerar Nova Oferta de Simulação", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                // Dynamic offer layout with 20s countdown!
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                    border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("🔔 CORRIDA DETECTADA!", fontWeight = FontWeight.Black, color = Color(0xFFD97706), fontSize = 13.sp)
                                            }
                                            Surface(color = Color(0xFFEF4444), shape = RoundedCornerShape(12.dp)) {
                                                Text(
                                                    text = "${offerCountdown}s",
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        Divider(color = Color(0xFFFDE68A))

                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("Loja: Pizza do Bairro 🍕", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("Distância de você: 1.2 km", fontSize = 12.sp, color = Color(0xFF64748B))
                                            Text("Endereço de Entrega: Rua das Acácias, 401 - Centro", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth().background(Color(0xFFFEF3C7)).padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("RECEBA DIRETAMENTE DA LOJA:", fontSize = 9.sp, color = Color(0xFF78350F), fontWeight = FontWeight.Black)
                                                Text("R$ 15,00", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFFB45309))
                                            }
                                            Text("Tempo estimado: 15 min", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                        }

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            OutlinedButton(
                                                onClick = { hasOffer = false },
                                                modifier = Modifier.weight(1f).height(44.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFFEF4444))
                                            ) {
                                                Text("Recusar", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = {
                                                    // Initialize active ride tied to the global order o1 for real-time customer loop
                                                    arrivedAtDestinationTime = null
                                                    arrivedAtDestinationLoc = null
                                                    isWaitingSimulated = false
                                                    waitTimeLeftSeconds = BairrooLogisticsSettings.waitTimeMinutes.value * 60
                                                    notificationLogs.clear()
                                                    isReturningToStore = false
                                                    activeRideDetail = MockRide(
                                                        id = "o1",
                                                        storeName = "Vila Burger",
                                                        distance = "1.2 km",
                                                        price = BairrooLogisticsSettings.regularDeliveryFee.value,
                                                        timeToArrive = "5 min",
                                                        customerName = "Rodrigo Silva",
                                                        customerPhone = "(37) 98877-1234",
                                                        customerAddress = "Avenida Central do Bairro, 1022 - Bloco C",
                                                        itemsSummary = "1x Vila Burger Clássico + Refrigerante Lata",
                                                        observations = "Entregar no Bloco C, interfone 302"
                                                    )
                                                    activeRideStatus = "ACCEPTED"
                                                    activeTab = "Corridas"
                                                    android.widget.Toast.makeText(context, "Corrida ACEITA! Desloque-se à loja.", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.weight(1.5f).height(44.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Aceitar Corrida 🛵", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Corridas" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (activeRideDetail == null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text("📦", fontSize = 48.sp)
                                        Text("Nenhuma corrida ativa", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                        Text("Fique online e aguarde novas chamadas na aba 'Início'.", fontSize = 12.sp, color = Color(0xFF64748B), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                        Button(
                                            onClick = { activeTab = "Início" },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D))
                                        ) {
                                            Text("Voltar para Início", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                val ride = activeRideDetail!!
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("VIAGEM ATIVA", fontWeight = FontWeight.Black, color = Color(0xFF0F532D), fontSize = 12.sp)
                                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp)) {
                                                Text(
                                                    text = when (activeRideStatus) {
                                                        "ACCEPTED" -> "Aceito: Ir até a Loja"
                                                        "ARRIVED" -> "Chegou: Retirar Pedido"
                                                        "PICKED_UP" -> "Pacote Coletado!"
                                                        "DELIVERING" -> "A caminho do Cliente"
                                                        else -> "Quase Concluído"
                                                    },
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF15803D)
                                                )
                                            }
                                        }

                                        Divider()

                                        // Store details
                                        Column {
                                            Text("PONTO DE RETIRADA (ESTABELECIMENTO)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF64748B))
                                            Text(ride.storeName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text("Distância: ${ride.distance}", fontSize = 11.sp, color = Color(0xFF475569))
                                        }

                                        // Delivery target details
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text("DADOS DA ENTREGA (CLIENTE)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF475569))
                                                Text(ride.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Icon(Icons.Default.Phone, null, tint = Color(0xFF1E293B), modifier = Modifier.size(14.dp))
                                                    Text(ride.customerPhone, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Icon(Icons.Default.DirectionsBike, null, tint = Color(0xFF1E293B), modifier = Modifier.size(14.dp))
                                                    Text(ride.customerAddress, fontSize = 12.sp)
                                                }

                                                Divider(color = Color.White)

                                                Text("Itens: ${ride.itemsSummary}", fontSize = 11.sp, color = Color(0xFF334155))
                                                Text("Obs: ${ride.observations}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                            }
                                        }

                                        // Financial model highlight (paid directly!)
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                                            border = BorderStroke(1.dp, Color(0xFFF59E0B))
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column {
                                                    Text("PAGAMENTO DIRETO DA LOJA:", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF78350F))
                                                    Text("R$ ${String.format("%.2f", ride.price)}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFB45309))
                                                }
                                                Text("Método: PIX/Dinheiro", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Sequential buttons
                                        when (activeRideStatus) {
                                            "ACCEPTED" -> {
                                                Button(
                                                    onClick = { activeRideStatus = "ARRIVED" },
                                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D))
                                                ) {
                                                    Text("Cheguei no Estabelecimento", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            "ARRIVED" -> {
                                                Button(
                                                    onClick = { activeRideStatus = "PICKED_UP" },
                                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D))
                                                ) {
                                                    Text("Retirei o Pedido", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            "PICKED_UP" -> {
                                                Button(
                                                    onClick = { activeRideStatus = "DELIVERING" },
                                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                                ) {
                                                    Text("Iniciei a Rota de Entrega", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            "DELIVERING" -> {
                                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                    if (arrivedAtDestinationTime == null) {
                                                        // Update: arrived at destination
                                                        Button(
                                                            onClick = { 
                                                                arrivedAtDestinationTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                                                                arrivedAtDestinationLoc = "-20.1438, -44.8902"
                                                                android.widget.Toast.makeText(context, "Chegada registrada via GPS!", android.widget.Toast.LENGTH_SHORT).show()
                                                            },
                                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D))
                                                        ) {
                                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                                Icon(Icons.Default.LocationOn, null, tint = Color.White)
                                                                Text("Cheguei ao Destino", fontWeight = FontWeight.Bold)
                                                            }
                                                        }
                                                    } else {
                                                        // Driver checked-in!
                                                        Surface(
                                                            color = Color(0xFFEFF6FF),
                                                            shape = RoundedCornerShape(10.dp),
                                                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Column(modifier = Modifier.padding(12.dp)) {
                                                                Text("📍 DESTINO ALCANÇADO", style = TextStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), fontSize = 12.sp))
                                                                Text("Chegada confirmada às: $arrivedAtDestinationTime", style = TextStyle(color = Color(0xFF1E40AF), fontSize = 11.sp))
                                                                Text("Coordenadas GPS registradas: $arrivedAtDestinationLoc", style = TextStyle(color = Color(0xFF1E40AF), fontSize = 11.sp))
                                                            }
                                                        }
                                                        
                                                        if (!isWaitingSimulated && !isReturningToStore) {
                                                            // Give option for successful delivery OR client absent
                                                            Button(
                                                                onClick = { activeRideStatus = "COMPLETED" },
                                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                                            ) {
                                                                Text("Finalizar Entrega Normal ✓", fontWeight = FontWeight.Bold)
                                                            }
                                                            
                                                            OutlinedButton(
                                                                onClick = { 
                                                                    isWaitingSimulated = true
                                                                    waitTimeLeftSeconds = BairrooLogisticsSettings.waitTimeMinutes.value * 60
                                                                    notificationLogs.clear()
                                                                    notificationLogs.add("⏱️ Cronômetro iniciado. Tempo padrão configurado: ${BairrooLogisticsSettings.waitTimeMinutes.value} minutos.")
                                                                },
                                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                                                            ) {
                                                                Text("Cliente Ausente / Iniciar Espera ⏳", fontWeight = FontWeight.Bold)
                                                            }
                                                        } else if (isWaitingSimulated) {
                                                            // Wait screen countdown!
                                                            Card(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                                                border = BorderStroke(1.5.dp, Color(0xFFF59E0B))
                                                            ) {
                                                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                            Icon(Icons.Default.HourglassTop, null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                                                            Text("Aguardando Cliente", fontWeight = FontWeight.Black, color = Color(0xFFB45309), fontSize = 12.sp)
                                                                        }
                                                                        
                                                                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp)) {
                                                                            Text(
                                                                                text = "%02d:%02d".format(waitTimeLeftSeconds / 60, waitTimeLeftSeconds % 60),
                                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                                                fontWeight = FontWeight.Black,
                                                                                color = Color(0xFFB45309),
                                                                                fontSize = 14.sp
                                                                            )
                                                                        }
                                                                    }
                                                                    
                                                                    Text("Simulador de Notificações Ativas ao Cliente:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                        notificationLogs.forEach { log ->
                                                                            Text("• $log", fontSize = 10.sp, color = Color(0xFF4B5563))
                                                                        }
                                                                    }
                                                                    
                                                                    if (waitTimeLeftSeconds > 0) {
                                                                        Button(
                                                                            onClick = { 
                                                                                waitTimeLeftSeconds = 0 
                                                                                notificationLogs.add("🚨 Tempo limite de 5 minutos excedido.")
                                                                                notificationLogs.add("📱 Cliente alertado sobre o retorno dos produtos à loja.")
                                                                            },
                                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                                                            modifier = Modifier.fillMaxWidth().height(34.dp)
                                                                        ) {
                                                                            Text("Acelerar Espera (Simular 5min)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                        }
                                                                    } else {
                                                                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                                                            Text(
                                                                                "⚠️ CLIENTE NÃO ENCONTRADO! Você pode retornar com segurança ao estabelecimento lojista.",
                                                                                color = Color(0xFF991B1B),
                                                                                fontSize = 11.sp,
                                                                                fontWeight = FontWeight.Bold,
                                                                                modifier = Modifier.padding(8.dp)
                                                                            )
                                                                        }
                                                                        
                                                                        Button(
                                                                            onClick = { 
                                                                                isWaitingSimulated = false
                                                                                isReturningToStore = true
                                                                            },
                                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                                                            modifier = Modifier.fillMaxWidth().height(44.dp)
                                                                        ) {
                                                                            Text("Retornar Pedido à Loja 🚗", fontWeight = FontWeight.Bold)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else if (isReturningToStore) {
                                                            // Retornando
                                                            Card(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                                                border = BorderStroke(1.5.dp, Color(0xFFFCA5A5))
                                                            ) {
                                                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                                    Text("📦 RETORNO ATIVO DE MERCADORIA", style = TextStyle(fontWeight = FontWeight.Black, color = Color(0xFF991B1B), fontSize = 12.sp))
                                                                    Text("Por favor, desloque-se de volta ao estabelecimento lojista para devolver as mercadorias.", fontSize = 11.sp, color = Color(0xFF7F1D1D))
                                                                    Text("Estabelecimento: Vila Burger", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                                    
                                                                    Button(
                                                                        onClick = {
                                                                            // 1. Update order status globally so lojista and customer see it
                                                                            BairrooOrderRegistry.updateStatus(ride.id, "Devolvido para Loja")
                                                                            
                                                                            // 2. Deliver gets paid as normal (o entregador mantem a remuneracao)
                                                                            val cut = ride.price * 0.15
                                                                            debtBalance += cut
                                                                            todayRidesCount += 1
                                                                            totalRidesCount += 1
                                                                            todayEarnings += ride.price
                                                                            
                                                                            // 3. Clear active ride
                                                                            activeRideDetail = null
                                                                            activeRideStatus = "NONE"
                                                                            hasOffer = false
                                                                            isReturningToStore = false
                                                                            arrivedAtDestinationTime = null
                                                                            arrivedAtDestinationLoc = null
                                                                            isWaitingSimulated = false
                                                                            activeTab = "Início"
                                                                            android.widget.Toast.makeText(context, "Devolução Confirmada! Sua remuneração de R$ ${String.format("%.2f", ride.price)} foi mantida.", android.widget.Toast.LENGTH_LONG).show()
                                                                        },
                                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                                                    ) {
                                                                        Text("Confirmar Entrega da Devolução na Loja ✓", fontWeight = FontWeight.Bold)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            "COMPLETED" -> {
                                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text("Confirmar Conclusão da Corrira:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    Text("Certifique-se de que a loja pagou o valor de R$ ${String.format("%.2f", ride.price)} diretamente em suas mãos antes de concluir.", fontSize = 11.sp, color = Color(0xFF64748B))
                                                    
                                                    Button(
                                                        onClick = {
                                                            // Calculate commission fee to hold in Bairroo debt balance (e.g. 15%)
                                                            val cut = ride.price * 0.15
                                                            debtBalance += cut
                                                            todayRidesCount += 1
                                                            totalRidesCount += 1
                                                            todayEarnings += ride.price
                                                            
                                                            // Clear active ride
                                                            activeRideDetail = null
                                                            activeRideStatus = "NONE"
                                                            hasOffer = false
                                                            activeTab = "Início"
                                                            android.widget.Toast.makeText(context, "Corrida Concluída! Ganhos atualizados.", android.widget.Toast.LENGTH_LONG).show()
                                                        },
                                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                                    ) {
                                                        Text("Confirmar e Concluir Corrida ✓", fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Ganhos" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Main financial details
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("PAINEL FINANCEIRO", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF0F532D))
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Corridas Totais", fontSize = 11.sp, color = Color(0xFF64748B))
                                            Text("$totalRidesCount", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        }
                                        Column {
                                            Text("Recebido das Lojas", fontSize = 11.sp, color = Color(0xFF64748B))
                                            val tot = totalRidesCount * 12.0
                                            Text("R$ ${String.format("%.2f", tot)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F532D))
                                        }
                                    }

                                    Divider()

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Participação Bairroo (Repasse Paga)", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                            Text("Taxa Fixa Ref. Ganhos (15%)", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        }
                                        Text("R$ ${String.format("%.2f", debtBalance)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFFFF9800))
                                    }

                                    Text("Importante: O Bairroo não retém pagamento em corridas. Toda cobrança de participação é acumulada em sua carteira e deve ser repassada via Pix periodicamente.", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            // repasse PIX block
                            if (debtBalance > 0.0) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                    border = BorderStroke(1.dp, Color(0xFFF59E0B))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text("REPASSE DE PARTICIPAÇÃO VIA PIX 🔑", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFB45309))
                                        Text("Realize o PIX abaixo para regularizar suas faturas pendentes e evitar rebaixamento de classificação ou bloqueios na plataforma.", fontSize = 11.sp, color = Color(0xFF78350F))

                                        // QR Code simulated drawing
                                        Box(
                                            modifier = Modifier
                                                .size(110.dp)
                                                .background(Color.White)
                                                .border(1.dp, Color(0xFFCBD5E1))
                                                .align(Alignment.CenterHorizontally),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Canvas(modifier = Modifier.size(90.dp)) {
                                                // Draws simulated barcode / qr square
                                                drawRect(color = Color.Black, size = this.size, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
                                                drawRect(color = Color.Black, size = this.size / 3f)
                                                // Random pixel simulations
                                                val square = this.size.width / 10f
                                                for (i in 0..9) {
                                                    for (j in 0..9) {
                                                        if ((i + j) % 3 == 0 || (i * j) % 4 == 0) {
                                                            drawRect(
                                                                color = Color.Black,
                                                                topLeft = androidx.compose.ui.geometry.Offset(i * square, j * square),
                                                                size = androidx.compose.ui.geometry.Size(square, square)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Copy PIX keys
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White)
                                                .border(1.dp, Color(0xFFE2E8F0))
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "pix.bairroo.com.br/repass_entregador_39kd0",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color(0xFF475569)
                                            )
                                            TextButton(
                                                onClick = {
                                                    android.widget.Toast.makeText(context, "Chave PIX Copiada com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                contentPadding = PaddingValues(1.dp)
                                            ) {
                                                Text("Copiar", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { paymentConfirmedByDriver = !paymentConfirmedByDriver }) {
                                            Checkbox(checked = paymentConfirmedByDriver, onCheckedChange = { paymentConfirmedByDriver = it })
                                            Text("Declaro que fiz o pagamento do repasse acima.", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                debtBalance = 0.0
                                                paymentConfirmedByDriver = false
                                                android.widget.Toast.makeText(context, "Sua situação financeira foi REGULARIZADA com sucesso! Corridas liberadas.", android.widget.Toast.LENGTH_LONG).show()
                                            },
                                            enabled = paymentConfirmedByDriver,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Confirmar Pagamento do Repasse ✓", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🎉", fontSize = 24.sp)
                                        Column {
                                            Text("PARABÉNS! VOCÊ ESTÁ EM DIA!", fontWeight = FontWeight.Black, color = Color(0xFF15803D), fontSize = 13.sp)
                                            Text("Sua conta não tem nenhum débito de participação pendente no momento.", fontSize = 11.sp, color = Color(0xFF166534))
                                        }
                                    }
                                }
                            }

                            // Period statements details list
                            Text("HISTÓRICO FINANCEIRO / FECHAMENTOS", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp))

                            listOf(
                                Triple("Período: 01/06 a 15/06", "30 Corridas • Recebimento R$ 450", "Pago • Pix"),
                                Triple("Período: 16/05 a 31/05", "42 Corridas • Recebimento R$ 630", "Pago • Pix"),
                                Triple("Período: 01/05 a 15/05", "25 Corridas • Recebimento R$ 375", "Pago • Pix")
                            ).forEach { stat ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(stat.first, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(stat.second, fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                            Text(stat.third, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(6.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Pedidos" -> {
                        DriverOrderListScreen(orderViewModel, driverId, onNavigateToWallet = { activeTab = "Ganhos" })
                    }

                    "Mais" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Driver profile & rating summary
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .background(Color(0xFF0F532D), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🧑‍✈️", fontSize = 28.sp)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Claudio Entregador", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                                Text("★ 4.9", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                            }
                                            Text("Pontualidade: 98%", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                    }
                                }
                            }

                            // Vehicle configuration form inside Part 2 requirements
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("INFORMAÇÕES DO VEÍCULO 🛵", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F532D))
                                    Text("Atualize os dados do transporte que você utiliza para fazer as entregas locais:", fontSize = 11.sp, color = Color(0xFF64748B))

                                    Text("Tipo de Veículo:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("Moto 🛵", "Carro 🚗", "Bike 🚲").forEach { type ->
                                            val isSel = vehicleType == type
                                            OutlinedButton(
                                                onClick = { vehicleType = type },
                                                modifier = Modifier.weight(1f).height(38.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isSel) Color(0xFFE2E8F0) else Color.Transparent),
                                                border = BorderStroke(1.dp, if (isSel) Color(0xFF0F532D) else Color(0xFFCBD5E1))
                                            ) {
                                                Text(type, fontSize = 10.sp, color = Color.Black, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = vehicleModel,
                                        onValueChange = { vehicleModel = it },
                                        label = { Text("Marca/Modelo do do veículo") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = vehiclePlate,
                                        onValueChange = { vehiclePlate = it },
                                        label = { Text("Placa do veículo") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = {
                                            android.widget.Toast.makeText(context, "Informações do veículo salvas!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F532D)),
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    ) {
                                        Text("Salvar Informações", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Reputation, milestones badges and stats!
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Cancelamento", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("1.2%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF10B981))
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Reclamações", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("0", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF10B981))
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Conclusão", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("100%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF10B981))
                                    }
                                }
                            }

                            // Simple options lists
                            listOf(
                                "Área de Atuação",
                                "Documentos Enviados",
                                "Notificações",
                                "Suporte & Central de Ajuda"
                            ).forEach { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            android.widget.Toast.makeText(context, "Recurso '$item' configurado!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(item, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF64748B))
                                    }
                                }
                            }

                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("Retornar ao Bairroo Cliente", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

