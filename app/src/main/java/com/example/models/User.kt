package com.example.models

data class BairrooAddress(
    val id: String,
    val type: String, // "Casa", "Trabalho", "Outro"
    val cep: String,
    val street: String = "",
    val number: String,
    val complement: String,
    val reference: String,
    val isPrimary: Boolean = false
)

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String, // "customer", "partner", "driver", "admin", "client"
    val permissions: List<String> = emptyList(),
    val phone: String = "",
    val createdAt: String = "",
    val active: Boolean = true,
    var profileComplete: Boolean = false,
    var photo: String? = null,
    val favorites: List<String> = emptyList(),
    val addresses: List<BairrooAddress> = emptyList(),
    var bairrooMais: Boolean = false,
    var points: Int = 0,
    var score: Int = 100,
    var membershipLevel: String = "Bairro", // "Bairro", "Bairrooo Mais", "Bairrooo Premium", "Bairrooo Elite"
    val membershipStartDate: String? = null,
    var membershipRenewDate: String? = null,
    val password: String = "senha123",
    val roles: List<String> = listOf(role)
)
