package com.example.nethesap.ui.unit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

data class UnitConverterState(
    val amount: String = "1",
    val category: UnitCategory = UnitCategory.LENGTH,
    val fromUnit: ConversionUnit = UnitCategory.LENGTH.units[0],
    val toUnit: ConversionUnit = UnitCategory.LENGTH.units[1],
    val result: String = ""
)

enum class UnitCategory(val title: String, val units: List<ConversionUnit>) {
    LENGTH("Uzunluk", listOf(
        ConversionUnit("Metre", "m", 1.0),
        ConversionUnit("Kilometre", "km", 1000.0),
        ConversionUnit("Santimetre", "cm", 0.01),
        ConversionUnit("Mil", "mi", 1609.34),
        ConversionUnit("İnç", "in", 0.0254)
    )),
    WEIGHT("Ağırlık", listOf(
        ConversionUnit("Kilogram", "kg", 1.0),
        ConversionUnit("Gram", "g", 0.001),
        ConversionUnit("Ton", "t", 1000.0),
        ConversionUnit("Libre", "lb", 0.453592),
        ConversionUnit("Ons", "oz", 0.0283495)
    )),
    TEMPERATURE("Sıcaklık", listOf(
        ConversionUnit("Celsius", "°C", 1.0),
        ConversionUnit("Fahrenheit", "°F", 1.0), // Özel hesaplama gerektirir
        ConversionUnit("Kelvin", "K", 1.0) // Özel hesaplama gerektirir
    )),
    AREA("Alan", listOf(
        ConversionUnit("Metrekare", "m²", 1.0),
        ConversionUnit("Hektar", "ha", 10000.0),
        ConversionUnit("Dönüm", "daa", 1000.0),
        ConversionUnit("Santimetrekare", "cm²", 0.0001)
    ))
}

data class ConversionUnit(val name: String, val symbol: String, val factor: Double)

@HiltViewModel
class UnitConverterViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(UnitConverterState())
    val state: State<UnitConverterState> = _state

    private val turkishFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR"))
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','
        DecimalFormat("#,##0.####", symbols)
    }

    private val inputFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR"))
        symbols.groupingSeparator = '.'
        DecimalFormat("#,###", symbols)
    }

    init {
        calculateResult()
    }

    fun onAmountChange(amount: String) {
        val clean = amount.replace(".", "")
        if (clean.all { it.isDigit() || it == ',' } && clean.count { it == ',' } <= 1) {
            val formatted = formatTurkishInput(clean)
            _state.value = _state.value.copy(amount = formatted)
            calculateResult()
        }
    }

    fun onCategoryChange(category: UnitCategory) {
        _state.value = _state.value.copy(
            category = category,
            fromUnit = category.units[0],
            toUnit = category.units[1]
        )
        calculateResult()
    }

    fun onFromUnitChange(unit: ConversionUnit) {
        _state.value = _state.value.copy(fromUnit = unit)
        calculateResult()
    }

    fun onToUnitChange(unit: ConversionUnit) {
        _state.value = _state.value.copy(toUnit = unit)
        calculateResult()
    }

    fun swapUnits() {
        _state.value = _state.value.copy(
            fromUnit = _state.value.toUnit,
            toUnit = _state.value.fromUnit
        )
        calculateResult()
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

    private fun calculateResult() {
        val amount = parseTurkish(_state.value.amount)
        val from = _state.value.fromUnit
        val to = _state.value.toUnit
        val category = _state.value.category

        val resultValue = if (category == UnitCategory.TEMPERATURE) {
            convertTemperature(amount, from.name, to.name)
        } else {
            (amount * from.factor) / to.factor
        }

        _state.value = _state.value.copy(result = turkishFormatter.format(resultValue))
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        // Hepsini önce Celsius'a çevir
        val celsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32) * 5 / 9
            "Kelvin" -> value - 273.15
            else -> value
        }
        // Celsius'tan hedefe çevir
        return when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> (celsius * 9 / 5) + 32
            "Kelvin" -> celsius + 273.15
            else -> celsius
        }
    }
}
