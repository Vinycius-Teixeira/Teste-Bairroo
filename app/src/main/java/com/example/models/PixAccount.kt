package com.example.models

data class PixAccount(
    val id: String, // "LOJISTAS", "ENTREGADORES", "BAIRROO_MAIS"
    val name: String,
    var type: String, // CPF, CNPJ, Telefone, Email, Chave Aleatória
    var key: String,
    var receiverName: String,
    var bank: String,
    var description: String,
    var qrCodeUrl: String?,
    var isActive: Boolean
)
