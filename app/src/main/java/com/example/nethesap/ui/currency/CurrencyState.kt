package com.example.nethesap.ui.currency

import com.example.nethesap.domain.model.Currency

data class CurrencyState(
    val isLoading: Boolean = false,
    val currencies: List<Currency> = emptyList(),
    val error: String = "",
    val amount: String = "100",
    val selectedCurrency: Currency? = null,
    val isTlrToSelected: Boolean = false,
    val result: String = ""
)
