package com.example.screens.partner

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.*
import com.example.components.FoodImagePlaceholder

@Composable
fun SejaParceiroScreenContent(
    currentChoice: String,
    onChoiceSet: (String) -> Unit,
    storeStep: Int,
    onStoreStepChange: (Int) -> Unit,
    driverStep: Int,
    onDriverStepChange: (Int) -> Unit,
    // Fields store
    companyName: String,
    onCompanyNameChange: (String) -> Unit,
    companyCategory: String,
    onCompanyCategoryChange: (String) -> Unit,
    responsibleName: String,
    onResponsibleNameChange: (String) -> Unit,
    companyCnpj: String,
    onCompanyCnpjChange: (String) -> Unit,
    companyPhone: String,
    onCompanyPhoneChange: (String) -> Unit,
    storeDocumentUploaded: Boolean,
    storeUploadingDoc: Boolean,
    onSimulateStoreUpload: () -> Unit,
    // Fields Driver
    selectedVehicle: String,
    onVehicleChange: (String) -> Unit,
    driverBrandModel: String,
    onDriverBrandModelChange: (String) -> Unit,
    driverPlate: String,
    onDriverPlateChange: (String) -> Unit,
    driverDocumentUploaded: Boolean,
    driverUploadingDoc: Boolean,
    onSimulateDriverUpload: () -> Unit,
    resetForm: () -> Unit,
    onNavigateDriverDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(screenFramePadding())
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        if (currentChoice == "NONE") {
            // Options Landing Screen
            Text(
                "Seja parceiro do Bairroo",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )
            Text(
                "Escolha como deseja fazer parte do nosso time e crescer com a gente de forma sustentável.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            // Quero ter uma loja
            PartnerSelectCard(
                title = "Quero ter uma loja",
                description = "Cadastre seu comércio no Bairroo, receba pedidos online e multiplique o seu faturamento.",
                imageType = "loja_illustration",
                btnText = "Cadastrar loja",
                onClick = { onChoiceSet("STORE") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quero ser entregador
            PartnerSelectCard(
                title = "Quero ser entregador",
                description = "Seja dono do seu tempo, faça entregas fáceis no bairro e tenha retornos imediatos.",
                imageType = "scooter_illustration",
                btnText = "Cadastrar entregador",
                onClick = { onChoiceSet("DRIVER") }
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Já tem cadastro? Acessar minha conta",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF22C55E), fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateDriverDashboard() }
            )
        } else if (currentChoice == "STORE") {
            // Store Wizard Process Steps 1 to 5
            StoreWizard(
                step = storeStep,
                onStepChange = onStoreStepChange,
                companyName = companyName,
                onCompanyNameChange = onCompanyNameChange,
                companyCategory = companyCategory,
                onCompanyCategoryChange = onCompanyCategoryChange,
                responsibleName = responsibleName,
                onResponsibleNameChange = onResponsibleNameChange,
                companyCnpj = companyCnpj,
                onCompanyCnpjChange = onCompanyCnpjChange,
                companyPhone = companyPhone,
                onCompanyPhoneChange = onCompanyPhoneChange,
                documentUploaded = storeDocumentUploaded,
                uploadingDoc = storeUploadingDoc,
                onSimulateStoreUpload = onSimulateStoreUpload,
                onCancel = resetForm
            )
        } else {
            // Driver Wizard Process Steps 1 to 5
            DriverWizard(
                step = driverStep,
                onStepChange = onDriverStepChange,
                selectedVehicle = selectedVehicle,
                onVehicleChange = onVehicleChange,
                brandModel = driverBrandModel,
                onBrandModelChange = onDriverBrandModelChange,
                plate = driverPlate,
                onPlateChange = onDriverPlateChange,
                documentUploaded = driverDocumentUploaded,
                uploadingDoc = driverUploadingDoc,
                onSimulateDriverUpload = onSimulateDriverUpload,
                onCancel = resetForm,
                onFinish = onNavigateDriverDashboard
            )
        }
    }
}

@Composable
fun PartnerSelectCard(
    title: String,
    description: String,
    imageType: String,
    btnText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                FoodImagePlaceholder(imageType, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(btnText, style = TextStyle(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun StoreWizard(
    step: Int,
    onStepChange: (Int) -> Unit,
    companyName: String,
    onCompanyNameChange: (String) -> Unit,
    companyCategory: String,
    onCompanyCategoryChange: (String) -> Unit,
    responsibleName: String,
    onResponsibleNameChange: (String) -> Unit,
    companyCnpj: String,
    onCompanyCnpjChange: (String) -> Unit,
    companyPhone: String,
    onCompanyPhoneChange: (String) -> Unit,
    documentUploaded: Boolean,
    uploadingDoc: Boolean,
    onSimulateStoreUpload: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cadastro de Loja",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onCancel) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancelar")
                }
            }
            
            Text(
                text = "Etapa $step de 5",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            // Step indication bar
            LinearProgressIndicator(
                progress = step.toFloat() / 5f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0xFFE5E7EB)
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            when (step) {
                1 -> {
                    Text("Dados da empresa", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = onCompanyNameChange,
                        label = { Text("Nome da empresa") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = responsibleName,
                        onValueChange = onResponsibleNameChange,
                        label = { Text("Nome do responsável") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                2 -> {
                    Text("Categoria do Negócio", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val categories = listOf("Mercado", "Restaurante", "Farmácia", "Bebidas", "Outro")
                    categories.forEach { cat ->
                        val isSel = companyCategory == cat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCompanyCategoryChange(cat) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSel, onClick = { onCompanyCategoryChange(cat) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                3 -> {
                    Text("Documentação Jurídica", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = companyCnpj,
                        onValueChange = onCompanyCnpjChange,
                        label = { Text("CNPJ / CPF") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = companyPhone,
                        onValueChange = onCompanyPhoneChange,
                        label = { Text("Telefone empresarial") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                4 -> {
                    Text("Upload de Documento", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Envie o contrato social ou comprovante de CNPJ", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280)))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFFAFAF8), RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = if (documentUploaded) Color(0xFF22C55E) else Color(0xFFD1D5DB),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSimulateStoreUpload() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uploadingDoc) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else if (documentUploaded) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Completo", tint = Color(0xFF22C55E))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("comprovante_cnpj.pdf - Carregado!", style = TextStyle(color = Color(0xFF14532D), fontWeight = FontWeight.Bold))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload", tint = Color.LightGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Clique para enviar documento", style = TextStyle(color = Color.Gray))
                            }
                        }
                    }
                }
                5 -> {
                    Text("Revisão de Cadastro", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Empresa: $companyName", style = MaterialTheme.typography.bodyMedium)
                    Text("Categoria: $companyCategory", style = MaterialTheme.typography.bodyMedium)
                    Text("Responsável: $responsibleName", style = MaterialTheme.typography.bodyMedium)
                    Text("Telefone: $companyPhone", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE2F0D9), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = Color(0xFF14532D))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "O cadastro está pronto. Seus dados e documentos serão revisados em até 24 horas no sistema Bairroo.",
                            color = Color(0xFF14532D),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Actions buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 1) {
                    Button(
                        onClick = { onStepChange(step - 1) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Anterior", color = Color.Black)
                    }
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Button(
                    onClick = {
                        if (step < 5) {
                            onStepChange(step + 1)
                        } else {
                            onCancel()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = when (step) {
                        1 -> companyName.isNotEmpty() && responsibleName.isNotEmpty()
                        4 -> documentUploaded
                        else -> true
                    }
                ) {
                    Text(if (step == 5) "Concluir" else "Continuar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DriverWizard(
    step: Int,
    onStepChange: (Int) -> Unit,
    selectedVehicle: String,
    onVehicleChange: (String) -> Unit,
    brandModel: String,
    onBrandModelChange: (String) -> Unit,
    plate: String,
    onPlateChange: (String) -> Unit,
    documentUploaded: Boolean,
    uploadingDoc: Boolean,
    onSimulateDriverUpload: () -> Unit,
    onCancel: () -> Unit,
    onFinish: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cadastro Entregador",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onCancel) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancelar")
                }
            }
            
            Text(
                text = "Etapa $step de 5",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFB923C), fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            // Step indication bar
            LinearProgressIndicator(
                progress = step.toFloat() / 5f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = Color(0xFFFB923C),
                trackColor = Color(0xFFE5E7EB)
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            when (step) {
                1 -> {
                    Text("Seu veículo", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        VehicleChoiceItem("Moto", Icons.Default.TwoWheeler, selectedVehicle == "Moto", { onVehicleChange("Moto") })
                        VehicleChoiceItem("Carro", Icons.Default.DirectionsCar, selectedVehicle == "Carro", { onVehicleChange("Carro") })
                        VehicleChoiceItem("Bicicleta", Icons.Default.PedalBike, selectedVehicle == "Bicicleta", { onVehicleChange("Bicicleta") })
                    }
                }
                2 -> {
                    Text("Detalhes do Veículo", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = brandModel,
                        onValueChange = onBrandModelChange,
                        label = { Text("Marca / Modelo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (selectedVehicle != "Bicicleta") {
                        OutlinedTextField(
                            value = plate,
                            onValueChange = onPlateChange,
                            label = { Text("Placa") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Text(
                            "Para bicicleta, não é necessário placa do veículo.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                }
                3 -> {
                    Text("Documentação (CNH)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Precisamos do envio da sua carteira de habilitação", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280)))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFFAFAF8), RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = if (documentUploaded) Color(0xFF22C55E) else Color(0xFFD1D5DB),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSimulateDriverUpload() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uploadingDoc) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else if (documentUploaded) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Completo", tint = Color(0xFF22C55E))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("documento_cnh.pdf - Carregado!", style = TextStyle(color = Color(0xFF14532D), fontWeight = FontWeight.Bold))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload", tint = Color.LightGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Clique para enviar documento", style = TextStyle(color = Color.Gray))
                            }
                        }
                    }
                }
                4 -> {
                    Text("Termos de Serviço", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Como entregador parceiro do Bairroo, você atua de maneira independente, gerenciando seus horários e mantendo a segurança no trânsito como prioridade absoluta.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF4B5563))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Check", tint = Color(0xFF22C55E))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Declaro que aceito as diretrizes logísticas.", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
                5 -> {
                    Text("Revisão Termos", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Veículo: $selectedVehicle ($brandModel)", style = MaterialTheme.typography.bodyMedium)
                    if (selectedVehicle != "Bicicleta") {
                        Text("Placa: $plate", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ElectricScooter, contentDescription = "Scooter", tint = Color(0xFFFB923C))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Pronto para faturar! Você será aprovado imediatamente e será redirecionado para o Painel do Entregador.",
                            color = Color(0xFFB45309),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Actions buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 1) {
                    Button(
                        onClick = { onStepChange(step - 1) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Anterior", color = Color.Black)
                    }
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Button(
                    onClick = {
                        if (step < 5) {
                            onStepChange(step + 1)
                        } else {
                            onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB923C)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = when (step) {
                        2 -> brandModel.isNotEmpty()
                        3 -> documentUploaded
                        else -> true
                    }
                ) {
                    Text(if (step == 5) "Finalizar" else "Continuar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun VehicleChoiceItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) Color(0xFFFB923C) else Color(0xFFE5E7EB)
    val bgColor = if (selected) Color(0xFFFFF7ED) else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color(0xFFFB923C) else Color(0xFF6B7280)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(84.dp)
            .background(bgColor, RoundedCornerShape(14.dp))
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        )
    }
}
