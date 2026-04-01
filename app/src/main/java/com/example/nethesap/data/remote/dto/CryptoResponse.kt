package com.example.nethesap.data.remote.dto

data class CryptoResponse(
    val success: Boolean,
    val result: List<CryptoDto>
)

data class CryptoDto(
    val name: String,
    val code: String,
    val price: Double,
    val change: Double,
    val marketcap: String? = null
)
