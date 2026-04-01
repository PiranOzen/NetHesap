package com.example.nethesap.ui.fuel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class FuelState(
    val distance: String = "",
    val consumption: String = "",
    val fuelPrice: String = "",
    val totalCost: Double = 0.0,
    val costPerKm: Double = 0.0
)

@HiltViewModel
class FuelViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(FuelState())
    val state: State<FuelState> = _state

    fun onDistanceChange(value: String) { _state.value = _state.value.copy(distance = value); calculate() }
    fun onConsumptionChange(value: String) { _state.value = _state.value.copy(consumption = value); calculate() }
    fun onPriceChange(value: String) { _state.value = _state.value.copy(fuelPrice = value); calculate() }

    private fun calculate() {
        val d = _state.value.distance.toDoubleOrNull() ?: 0.0
        val c = _state.value.consumption.toDoubleOrNull() ?: 0.0
        val p = _state.value.fuelPrice.toDoubleOrNull() ?: 0.0

        if (d > 0 && c > 0 && p > 0) {
            val totalFuel = (d / 100.0) * c
            val totalCost = totalFuel * p
            _state.value = _state.value.copy(
                totalCost = totalCost,
                costPerKm = totalCost / d
            )
        } else {
            _state.value = _state.value.copy(totalCost = 0.0, costPerKm = 0.0)
        }
    }
}
