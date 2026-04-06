package com.example.nethesap.ui.gold

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

data class GoldCalculatorState(
    val gramPrice: String = "",
    val results: List<GoldCalcResult> = emptyList()
)

data class GoldCalcResult(
    val name: String,
    val value: String,
    val description: String = ""
)

@HiltViewModel
class GoldCalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(GoldCalculatorState())
    val state: State<GoldCalculatorState> = _state

    private val turkishFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR"))
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','
        DecimalFormat("#,##0.00", symbols)
    }

    fun onGramPriceChange(price: String) {
        val clean = price.replace(".", "").replace(",", ".")
        if (price.isEmpty() || clean.toDoubleOrNull() != null || price.endsWith(",")) {
            _state.value = _state.value.copy(gramPrice = price)
            calculate(price)
        }
    }

    private fun calculate(priceStr: String) {
        val p = priceStr.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
        if (p <= 0) {
            _state.value = _state.value.copy(results = emptyList())
            return
        }

        // 24 Ayar saf altın fiyatı p olsun.
        val pureGram = p
        val carat22 = p * (22.0 / 24.0)
        
        val list = listOf(
            GoldCalcResult("22 Ayar Gram", turkishFormatter.format(carat22) + " ₺", "1 gram 22 ayar altın fiyatı"),
            GoldCalcResult("Çeyrek Altın", turkishFormatter.format(carat22 * 1.75) + " ₺", "1.75 gr / 22 Ayar"),
            GoldCalcResult("Yarım Altın", turkishFormatter.format(carat22 * 3.50) + " ₺", "3.50 gr / 22 Ayar"),
            GoldCalcResult("Tam Altın", turkishFormatter.format(carat22 * 7.01) + " ₺", "7.01 gr / 22 Ayar"),
            GoldCalcResult("Cumhuriyet Altını", turkishFormatter.format(carat22 * 7.21) + " ₺", "7.21 gr / 22 Ayar (Ata Lira)"),
            GoldCalcResult("22 Ayar Bilezik (20 gr)", turkishFormatter.format(carat22 * 20) + " ₺", "20 gram 22 ayar bilezik"),
            GoldCalcResult("22 Ayar Bilezik (25 gr)", turkishFormatter.format(carat22 * 25) + " ₺", "25 gram 22 ayar bilezik"),
            GoldCalcResult("22 Ayar Bilezik (30 gr)", turkishFormatter.format(carat22 * 30) + " ₺", "30 gram 22 ayar bilezik"),
            GoldCalcResult("18 Ayar Gram", turkishFormatter.format(p * (18.0 / 24.0)) + " ₺", "1 gram 18 ayar altın fiyatı"),
            GoldCalcResult("14 Ayar Gram", turkishFormatter.format(p * (14.0 / 24.0)) + " ₺", "1 gram 14 ayar altın fiyatı")
        )

        _state.value = _state.value.copy(results = list)
    }
}
