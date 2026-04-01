package com.example.nethesap.ui.interest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

data class InterestState(
    val principal: String = "",
    val interestRate: String = "",
    val duration: String = "",
    val durationType: DurationType = DurationType.MONTH,
    val isLoading: Boolean = false,
    val result: InterestResult? = null
)

data class InterestResult(
    val totalAmount: String,
    val totalInterest: String,
    val netInterest: String,
    val taxAmount: String
)

enum class DurationType {
    DAY, MONTH, YEAR
}

@HiltViewModel
class InterestViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(InterestState())
    val state: State<InterestState> = _state

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

    fun onPrincipalChange(value: String) {
        val clean = value.replace(".", "")
        if (clean.all { it.isDigit() || it == ',' } && clean.count { it == ',' } <= 1) {
            val formatted = formatTurkishInput(clean)
            _state.value = _state.value.copy(principal = formatted)
            calculateInterest()
        }
    }

    fun onInterestRateChange(value: String) {
        // Faiz oranında binlik ayırıcıya genelde gerek yok ama virgül desteği önemli
        if (value.all { it.isDigit() || it == ',' } && value.count { it == ',' } <= 1) {
            _state.value = _state.value.copy(interestRate = value)
            calculateInterest()
        }
    }

    fun onDurationChange(value: String) {
        if (value.all { it.isDigit() }) {
            _state.value = _state.value.copy(duration = value)
            calculateInterest()
        }
    }

    fun onDurationTypeChange(type: DurationType) {
        _state.value = _state.value.copy(durationType = type)
        calculateInterest()
    }

    private fun calculateInterest() {
        val p = parseTurkish(_state.value.principal)
        val r = parseTurkish(_state.value.interestRate)
        val t = _state.value.duration.toDoubleOrNull() ?: 0.0

        if (p == 0.0 || r == 0.0 || t == 0.0) {
            _state.value = _state.value.copy(result = null)
            return
        }

        val days = when (_state.value.durationType) {
            DurationType.DAY -> t
            DurationType.MONTH -> t * 30
            DurationType.YEAR -> t * 365
        }

        val grossInterest = (p * r * days) / 36500
        val taxRate = 0.05
        val taxAmount = grossInterest * taxRate
        val netInterest = grossInterest - taxAmount
        val totalAmount = p + netInterest

        _state.value = _state.value.copy(
            result = InterestResult(
                totalAmount = turkishFormatter.format(totalAmount),
                totalInterest = turkishFormatter.format(grossInterest),
                netInterest = turkishFormatter.format(netInterest),
                taxAmount = turkishFormatter.format(taxAmount)
            )
        )
    }
}
