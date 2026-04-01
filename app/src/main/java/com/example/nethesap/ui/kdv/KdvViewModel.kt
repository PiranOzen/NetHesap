package com.example.nethesap.ui.kdv

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class KdvState(
    val amount: String = "",
    val kdvRate: Double = 20.0,
    val kdvIncluded: Double = 0.0,
    val kdvExcluded: Double = 0.0,
    val kdvAmount: Double = 0.0,
    val isKdvIncludedInput: Boolean = false
)

@HiltViewModel
class KdvViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(KdvState())
    val state: State<KdvState> = _state

    fun onAmountChange(amount: String) {
        _state.value = _state.value.copy(amount = amount)
        calculate()
    }

    fun onRateChange(rate: Double) {
        _state.value = _state.value.copy(kdvRate = rate)
        calculate()
    }

    fun onInputTypeChange(isIncluded: Boolean) {
        _state.value = _state.value.copy(isKdvIncludedInput = isIncluded)
        calculate()
    }

    private fun calculate() {
        val input = _state.value.amount.replace(",", ".").toDoubleOrNull() ?: 0.0
        val rate = _state.value.kdvRate / 100.0

        if (_state.value.isKdvIncludedInput) {
            // Girilen tutar KDV dahil
            val excluded = input / (1 + rate)
            _state.value = _state.value.copy(
                kdvIncluded = input,
                kdvExcluded = excluded,
                kdvAmount = input - excluded
            )
        } else {
            // Girilen tutar KDV hariç
            val kdv = input * rate
            _state.value = _state.value.copy(
                kdvIncluded = input + kdv,
                kdvExcluded = input,
                kdvAmount = kdv
            )
        }
    }
}
