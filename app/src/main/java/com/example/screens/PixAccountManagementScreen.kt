package com.example.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.PixAccount

val pixAccounts = androidx.compose.runtime.mutableStateListOf(
    PixAccount("LOJISTAS", "Recebimento Lojistas", "CNPJ", "12.345.678/0001-99", "Bairroo Intermediation", "Banco do Brasil", "Comissões e Promoções", null, true),
    PixAccount("ENTREGADORES", "Recebimento Entregadores", "CPF", "123.456.789-00", "Bairroo Logística", "Nubank", "Repasses de corridas", null, true),
    PixAccount("BAIRROO_MAIS", "Recebimento Bairrooo Mais", "Chave Aleatória", "abc-123-def", "Bairroo App", "Itaú", "Assinaturas", null, true)
)

@Composable
fun PixAccountManagementScreen() {
    var editingAccount by remember { mutableStateOf<PixAccount?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Gerenciamento de PIX", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(pixAccounts) { account ->
                Card(modifier = Modifier.fillMaxWidth().clickable { editingAccount = account }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(account.name, fontWeight = FontWeight.Bold)
                            Switch(checked = account.isActive, onCheckedChange = { account.isActive = it })
                        }
                        Text("Chave: ${account.key} (${account.type})")
                        Text("Recebedor: ${account.receiverName}")
                        Text("Banco: ${account.bank}")
                    }
                }
            }
        }
    }

    if (editingAccount != null) {
        var key by remember(editingAccount) { mutableStateOf(editingAccount!!.key) }
        var receiverName by remember(editingAccount) { mutableStateOf(editingAccount!!.receiverName) }
        var bank by remember(editingAccount) { mutableStateOf(editingAccount!!.bank) }

        AlertDialog(
            onDismissRequest = { editingAccount = null },
            title = { Text("Editar ${editingAccount!!.name}") },
            text = {
                Column {
                    OutlinedTextField(value = key, onValueChange = { key = it }, label = { Text("Chave PIX") })
                    OutlinedTextField(value = receiverName, onValueChange = { receiverName = it }, label = { Text("Nome Recebedor") })
                    OutlinedTextField(value = bank, onValueChange = { bank = it }, label = { Text("Banco") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    editingAccount!!.key = key
                    editingAccount!!.receiverName = receiverName
                    editingAccount!!.bank = bank
                    editingAccount = null
                }) { Text("Salvar") }
            },
            dismissButton = { TextButton(onClick = { editingAccount = null }) { Text("Cancelar") } }
        )
    }
}
