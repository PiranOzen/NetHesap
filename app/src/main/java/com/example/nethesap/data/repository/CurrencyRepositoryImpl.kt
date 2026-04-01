package com.example.nethesap.data.repository

import com.example.nethesap.data.remote.TcmbApi
import com.example.nethesap.domain.model.Currency
import com.example.nethesap.domain.repository.CurrencyRepository
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: TcmbApi
) : CurrencyRepository {
    override suspend fun getCurrencies(): List<Currency> {
        val response = api.getCurrencies()
        return response.currencyList?.map { dto ->
            Currency(
                code = dto.code ?: "",
                name = dto.name ?: "",
                buyingPrice = dto.buying ?: "0.0",
                sellingPrice = dto.selling ?: "0.0"
            )
        } ?: emptyList()
    }
}
