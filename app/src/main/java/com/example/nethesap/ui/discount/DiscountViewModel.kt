package com.example.nethesap.ui.discount

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class DiscountState(
    val price: String = "",
    val discountRate: String = "",
    val discountedPrice: Double = 0.0,
    val savingAmount: Double = 0.0,
    val cost: String = "",
    val sellPrice: String = "",
    val profitAmount: Double = 0.0,
    val profitRate: Double = 0.0
)

@HiltViewModel
class DiscountViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(DiscountState())
    val state: State<DiscountState> = _state

    fun onPriceChange(value: String) { _state.value = _state.value.copy(price = value); calculateDiscount() }
    fun onDiscountRateChange(value: String) { _state.value = _state.value.copy(discountRate = value); calculateDiscount() }
    
    fun onCostChange(value: String) { _state.value = _state.value.copy(cost = value); calculateProfit() }
    fun onSellPriceChange(value: String) { _state.value = _state.value.copy(sellPrice = value); calculateProfit() }

    private fun calculateDiscount() {
        val p = _state.value.price.toDoubleOrNull() ?: 0.0
        val r = _state.value.discountRate.toDoubleOrNull() ?: 0.0
        if (p > 0 && r >= 0) {
            val saving = p * (r / 100.0)
            _state.value = _state.value.copy(
                discountedPrice = p - saving,
                savingAmount = saving
            )
        } else {
            _state.value = _state.value.copy(discountedPrice = 0.0, savingAmount = 0.0)
        }
    }

    private fun calculateProfit() {
        val c = _state.value.cost.toDoubleOrNull() ?: 0.0
        val s = _state.value.sellPrice.toDoubleOrNull() ?: 0.0
        if (c > 0 && s > 0) {
            val profit = s - c
            _state.value = _state.value.copy(
                profitAmount = profit,
                profitRate = (profit / c) * 100.0
            )
        } else {
            _state.value = _state.value.copy(profitAmount = 0.0, profitRate = 0.0)
        }
    }
}
