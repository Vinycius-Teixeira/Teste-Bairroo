package com.example.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.WalletTransaction
import com.example.viewmodels.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(viewModel: WalletViewModel, driverId: String) {
    val wallet by viewModel.wallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    LaunchedEffect(driverId) {
        viewModel.loadWallet(driverId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Carteira Bairrooo") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Saldo Disponível", style = MaterialTheme.typography.titleMedium)
            Text("R$ ${wallet?.balance ?: 0.0}", style = MaterialTheme.typography.headlineLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = { viewModel.recharge(driverId, 20.0) }) {
                Text("Recarregar R$ 20,00")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Histórico", style = MaterialTheme.typography.titleMedium)
            
            androidx.compose.foundation.lazy.LazyColumn {
                items(transactions.size) { index ->
                    val t = transactions[index]
                    ListItem(
                        headlineContent = { Text(t.type) },
                        supportingContent = { Text(t.reason) },
                        trailingContent = { Text("R$ ${t.amount}") }
                    )
                }
            }
        }
    }
}
