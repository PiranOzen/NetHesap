package com.example.nethesap.domain.repository

import com.example.nethesap.domain.model.Currency

interface CurrencyRepository {
    suspend fun getCurrencies(): List<Currency>
}
