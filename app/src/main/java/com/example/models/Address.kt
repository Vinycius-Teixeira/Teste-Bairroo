package com.example.models

data class Address(
    val id: String = "",
    val title: String = "",
    val cep: String = "",
    val street: String = "",
    val number: String = "",
    val complement: String = "",
    val reference: String = "",
    val city: String = "",
    val state: String = "",
    val isPrimary: Boolean = false
)
