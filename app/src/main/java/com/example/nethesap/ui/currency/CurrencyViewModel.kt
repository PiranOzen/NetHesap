package com.example.nethesap.ui.currency

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nethesap.domain.model.Currency
import com.example.nethesap.domain.use_case.GetCurrenciesUseCase
import com.example.nethesap.domain.use_case.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(CurrencyState())
    val state: State<CurrencyState> = _state

    init {
        getCurrencies()
    }

    fun getCurrencies() {
        getCurrenciesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val currencies = result.data ?: emptyList()
                    val selected = _state.value.selectedCurrency ?: currencies.find { it.code == "USD" }
                    _state.value = _state.value.copy(
                        currencies = currencies,
                        selectedCurrency = selected,
                        isLoading = false
                    )
                    calculateResult()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "Beklenmedik bir hata oluştu",
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onAmountChange(amount: String) {
        _state.value = _state.value.copy(amount = amount)
        calculateResult()
    }

    fun onCurrencySelect(currency: Currency) {
        _state.value = _state.value.copy(selectedCurrency = currency)
        calculateResult()
    }

    fun onToggleDirection() {
        _state.value = _state.value.copy(isTlrToSelected = !_state.value.isTlrToSelected)
        calculateResult()
    }

    private fun calculateResult() {
        val state = _state.value
        val amount = state.amount.toDoubleOrNull() ?: 0.0
        val currency = state.selectedCurrency ?: return

        // TCMB prices are often strings with dots/commas, let's assume standard double strings for now
        // If they have commas, we might need to replace them.
        val priceStr = if (state.isTlrToSelected) currency.sellingPrice else currency.buyingPrice
        val price = priceStr.replace(",", ".").toDoubleOrNull() ?: 0.0

        if (price == 0.0) {
            _state.value = _state.value.copy(result = "0.00")
            return
        }

        val resultValue = if (state.isTlrToSelected) {
            amount / price
        } else {
            amount * price
        }

        _state.value = _state.value.copy(result = String.format("%.2f", resultValue))
    }
}
