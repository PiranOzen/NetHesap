package com.example.nethesap.ui.loan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.pow

data class LoanState(
    val amount: String = "",
    val interestRate: String = "",
    val month: String = "",
    val monthlyPayment: Double = 0.0,
    val totalPayment: Double = 0.0,
    val totalInterest: Double = 0.0
)

@HiltViewModel
class LoanViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(LoanState())
    val state: State<LoanState> = _state

    fun onAmountChange(value: String) { _state.value = _state.value.copy(amount = value); calculate() }
    fun onInterestChange(value: String) { _state.value = _state.value.copy(interestRate = value); calculate() }
    fun onMonthChange(value: String) { _state.value = _state.value.copy(month = value); calculate() }

    private fun calculate() {
        val p = _state.value.amount.toDoubleOrNull() ?: 0.0
        val monthlyRate = (_state.value.interestRate.toDoubleOrNull() ?: 0.0) / 100.0
        val n = _state.value.month.toDoubleOrNull() ?: 0.0

        if (p > 0 && monthlyRate > 0 && n > 0) {
            val monthlyPayment = (p * monthlyRate * (1 + monthlyRate).pow(n)) / ((1 + monthlyRate).pow(n) - 1)
            val totalPayment = monthlyPayment * n
            _state.value = _state.value.copy(
                monthlyPayment = monthlyPayment,
                totalPayment = totalPayment,
                totalInterest = totalPayment - p
            )
        } else {
            _state.value = _state.value.copy(monthlyPayment = 0.0, totalPayment = 0.0, totalInterest = 0.0)
        }
    }
}
