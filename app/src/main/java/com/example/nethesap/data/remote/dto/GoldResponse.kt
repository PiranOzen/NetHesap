package com.example.nethesap.data.remote.dto

data class GoldResponse(
    val success: Boolean,
    val result: List<GoldDto>
)

data class GoldDto(
    val name: String,
    val buying: Double,
    val selling: Double,
    val buyingstr: String?,
    val sellingstr: String?,
    val time: String?
)
