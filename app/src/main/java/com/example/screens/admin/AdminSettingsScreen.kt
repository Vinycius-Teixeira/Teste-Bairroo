package com.example.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.AdminSettings
import com.example.viewmodels.AdminSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(viewModel: AdminSettingsViewModel) {
    val settings by viewModel.settings.collectAsState()
    var minRecharge by remember { mutableStateOf(settings.minRechargeAmount.toString()) }
    var feePerDelivery by remember { mutableStateOf(settings.feePerDelivery.toString()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Configurações Financeiras") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = minRecharge,
                onValueChange = { minRecharge = it },
                label = { Text("Valor Mínimo de Recarga") }
            )
            OutlinedTextField(
                value = feePerDelivery,
                onValueChange = { feePerDelivery = it },
                label = { Text("Valor Cobrado por Entrega") }
            )
            Button(onClick = {
                viewModel.updateSettings(
                    settings.copy(
                        minRechargeAmount = minRecharge.toDoubleOrNull() ?: 10.0,
                        feePerDelivery = feePerDelivery.toDoubleOrNull() ?: 2.0
                    )
                )
            }) {
                Text("Salvar Configurações")
            }
        }
    }
}
