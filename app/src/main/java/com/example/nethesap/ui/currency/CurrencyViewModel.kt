package com.example.nethesap.ui.currency

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nethesap.domain.model.Currency
import com.example.nethesap.domain.use_case.GetCurrenciesUseCase
import com.example.nethesap.domain.use_case.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(CurrencyState())
    val state: State<CurrencyState> = _state
    
    private var getCurrenciesJob: Job? = null

    private val turkishFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR"))
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','
        DecimalFormat("#,##0.00", symbols)
    }

    private val inputFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR"))
        symbols.groupingSeparator = '.'
        DecimalFormat("#,###", symbols)
    }

    init {
        getCurrencies()
    }

    private fun formatTurkishInput(input: String): String {
        if (input.isEmpty()) return ""
        val clean = input.replace(".", "")
        val parts = clean.split(",")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) "," + parts.getOrNull(1).orEmpty() else if (clean.endsWith(",")) "," else ""
        
        val formattedInteger = if (integerPart.isEmpty()) "" else {
            integerPart.toLongOrNull()?.let { inputFormatter.format(it) } ?: integerPart
        }
        
        return formattedInteger + decimalPart
    }

    private fun parseTurkish(value: String): Double {
        return value.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
    }

    fun getCurrencies() {
        getCurrenciesJob?.cancel()
        getCurrenciesJob = getCurrenciesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val currencies = result.data ?: emptyList()
                    val currentSelected = _state.value.selectedCurrency
                    val selected = currencies.find { it.code == currentSelected?.code } 
                                  ?: currencies.find { it.code == "USD" }
                                  ?: currencies.firstOrNull()

                    _state.value = _state.value.copy(
                        currencies = currencies,
                        selectedCurrency = selected,
                        isLoading = false,
                        error = ""
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
        val clean = amount.replace(".", "")
        if (clean.all { it.isDigit() || it == ',' } && clean.count { it == ',' } <= 1) {
            val formatted = formatTurkishInput(clean)
            _state.value = _state.value.copy(amount = formatted)
            calculateResult()
        }
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
        val amount = parseTurkish(state.amount)
        val currency = state.selectedCurrency ?: return

        val priceStr = if (state.isTlrToSelected) currency.sellingPrice else currency.buyingPrice
        val price = priceStr.replace(",", ".").toDoubleOrNull() ?: 0.0

        if (price == 0.0) {
            _state.value = _state.value.copy(result = "0,00")
            return
        }

        val resultValue = if (state.isTlrToSelected) {
            amount / price
        } else {
            amount * price
        }

        _state.value = _state.value.copy(result = turkishFormatter.format(resultValue))
    }
}
