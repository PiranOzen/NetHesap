package com.example.nethesap.ui.crypto

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nethesap.data.remote.CollectApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

data class CryptoState(
    val isLoading: Boolean = false,
    val cryptoItems: List<CryptoItem> = emptyList(),
    val error: String = "",
    val isShowingDemoData: Boolean = false
)

data class CryptoItem(
    val name: String,
    val code: String,
    val price: String,
    val change: String
)

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val api: CollectApi
) : ViewModel() {

    private val _state = mutableStateOf(CryptoState())
    val state: State<CryptoState> = _state

    private val turkishFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("tr", "TR"))
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','
        DecimalFormat("#,##0.00", symbols)
    }

    init {
        getCryptoPrices()
    }

    fun getCryptoPrices() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "", isShowingDemoData = false)
            try {
                // CollectAPI anahtarı "YOUR_API_KEY_HERE" olduğu sürece 401 hatası alınacaktır.
                val response = api.getCryptoPrices(CollectApi.API_KEY)
                if (response.success) {
                    val items = response.result.map {
                        CryptoItem(
                            name = it.name,
                            code = it.code,
                            price = turkishFormatter.format(it.price),
                            change = String.format(Locale("en", "US"), "%.2f", it.change)
                        )
                    }
                    _state.value = _state.value.copy(
                        cryptoItems = items, 
                        isLoading = false,
                        isShowingDemoData = false
                    )
                } else {
                    fetchDemoData()
                }
            } catch (e: Exception) {
                // 401 Unauthorized veya bağlantı hatası durumunda kullanıcıya boş ekran göstermemek için demo verileri yükle
                fetchDemoData()
            }
        }
    }

    private fun fetchDemoData() {
        val demoItems = listOf(
            CryptoItem("Bitcoin", "BTC", "96.450,00", "2.45"),
            CryptoItem("Ethereum", "ETH", "2.740,50", "-1.20"),
            CryptoItem("Solana", "SOL", "235,15", "5.67"),
            CryptoItem("Binance Coin", "BNB", "612,40", "0.85"),
            CryptoItem("Ripple", "XRP", "1,12", "-0.45"),
            CryptoItem("Cardano", "ADA", "0,78", "3.21"),
            CryptoItem("Avalanche", "AVAX", "42,65", "1.12"),
            CryptoItem("Polkadot", "DOT", "9,15", "-2.34"),
            CryptoItem("Chainlink", "LINK", "15,20", "4.15"),
            CryptoItem("Polygon", "MATIC", "0,45", "-0.80")
        )
        _state.value = _state.value.copy(
            cryptoItems = demoItems,
            isLoading = false,
            error = "",
            isShowingDemoData = true
        )
    }
}
