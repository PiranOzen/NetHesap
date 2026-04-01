package com.example.nethesap.ui.gold

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nethesap.data.remote.CollectApi
import com.example.nethesap.domain.use_case.GetCurrenciesUseCase
import com.example.nethesap.domain.use_case.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

data class GoldState(
    val isLoading: Boolean = false,
    val goldItems: List<GoldItem> = emptyList(),
    val error: String = "",
    val isRealTime: Boolean = false,
    val isShowingDemoData: Boolean = false
)

data class GoldItem(
    val name: String,
    val buyingPrice: String,
    val sellingPrice: String,
    val unit: String = "₺",
    val isRealTime: Boolean = false
)

@HiltViewModel
class GoldViewModel @Inject constructor(
    private val collectApi: CollectApi,
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(GoldState())
    val state: State<GoldState> = _state

    private val turkishFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR"))
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','
        DecimalFormat("#,##0.00", symbols)
    }

    init {
        getGoldPrices()
    }

    fun getGoldPrices() {
        fetchRealTimePrices()
    }

    private fun fetchRealTimePrices() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "", isShowingDemoData = false)
            try {
                // CollectAPI'den gerçek verileri çekiyoruz. API_KEY CollectApi.kt içerisinde.
                val response = collectApi.getGoldPrices(CollectApi.API_KEY)
                if (response.success && response.result.isNotEmpty()) {
                    val items = response.result.map {
                        GoldItem(
                            name = it.name,
                            buyingPrice = turkishFormatter.format(it.buying),
                            sellingPrice = turkishFormatter.format(it.selling),
                            isRealTime = true
                        )
                    }
                    _state.value = _state.value.copy(
                        goldItems = items, 
                        isLoading = false, 
                        isRealTime = true,
                        isShowingDemoData = false
                    )
                } else {
                    // API başarısız ise demo verilere dön
                    fetchEstimatedPrices()
                }
            } catch (e: Exception) {
                // Hata durumunda demo verilere dön
                fetchEstimatedPrices()
            }
        }
    }

    private fun fetchEstimatedPrices() {
        getCurrenciesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val currencies = result.data ?: emptyList()
                    val usdPrice = currencies.find { it.code == "USD" }?.sellingPrice?.replace(",", ".")?.toDoubleOrNull() ?: 34.50
                    
                    val estimatedItems = listOf(
                        GoldItem("Gram Altın", turkishFormatter.format(usdPrice * 85.5), turkishFormatter.format(usdPrice * 86.2)),
                        GoldItem("Çeyrek Altın", turkishFormatter.format(usdPrice * 140.5), turkishFormatter.format(usdPrice * 145.0)),
                        GoldItem("Yarım Altın", turkishFormatter.format(usdPrice * 281.0), turkishFormatter.format(usdPrice * 290.0)),
                        GoldItem("Tam Altın", turkishFormatter.format(usdPrice * 562.0), turkishFormatter.format(usdPrice * 580.0)),
                        GoldItem("Cumhuriyet Altını", turkishFormatter.format(usdPrice * 585.0), turkishFormatter.format(usdPrice * 605.0)),
                        GoldItem("22 Ayar Bilezik (gr)", turkishFormatter.format(usdPrice * 78.5), turkishFormatter.format(usdPrice * 82.0))
                    )
                    _state.value = _state.value.copy(
                        goldItems = estimatedItems, 
                        isLoading = false,
                        isRealTime = false,
                        isShowingDemoData = true
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "Hata oluştu", 
                        isLoading = false,
                        isShowingDemoData = true
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}
