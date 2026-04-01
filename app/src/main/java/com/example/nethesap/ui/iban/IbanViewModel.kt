package com.example.nethesap.ui.iban

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.math.BigInteger

data class IbanState(
    val iban: String = "",
    val isValid: Boolean? = null,
    val bankName: String = "",
    val country: String = "",
    val error: String = ""
)

@HiltViewModel
class IbanViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(IbanState())
    val state: State<IbanState> = _state

    private val banks = mapOf(
        "0001" to "T.C. Ziraat Bankası",
        "0010" to "T.C. Ziraat Bankası",
        "0012" to "Halkbank",
        "0015" to "Vakıfbank",
        "0032" to "TEB",
        "0046" to "Akbank",
        "0062" to "Garanti BBVA",
        "0064" to "İş Bankası",
        "0067" to "Yapı Kredi",
        "0092" to "QNB Finansbank",
        "0111" to "Denizbank",
        "0123" to "HSBC",
        "0134" to "Denizbank",
        "0205" to "Kuveyt Türk",
        "0206" to "Türkiye Finans",
        "0210" to "Albaraka Türk"
    )

    fun onIbanChange(value: String) {
        val cleanIban = value.uppercase().replace(" ", "").filter { it.isLetterOrDigit() }
        _state.value = _state.value.copy(iban = cleanIban, error = "")
        if (cleanIban.length >= 26) {
            validateIban(cleanIban)
        } else {
            _state.value = _state.value.copy(isValid = null, bankName = "", country = "")
        }
    }

    private fun validateIban(iban: String) {
        if (!iban.startsWith("TR")) {
            _state.value = _state.value.copy(isValid = false, error = "Yalnızca TR IBAN desteği bulunmaktadır.")
            return
        }
        if (iban.length != 26) {
            _state.value = _state.value.copy(isValid = false, error = "TR IBAN 26 karakter olmalıdır.")
            return
        }

        // Mod 97 Check
        val rearranged = iban.substring(4) + iban.substring(0, 4)
        val numeric = rearranged.map { 
            if (it.isLetter()) (it.code - 'A'.code + 10).toString() else it.toString()
        }.joinToString("")
        
        val bigInt = BigInteger(numeric)
        val isValid = bigInt.mod(BigInteger.valueOf(97)) == BigInteger.ONE

        if (isValid) {
            val bankCode = iban.substring(4, 9)
            val bankName = banks[bankCode] ?: "Bilinmeyen Banka ($bankCode)"
            _state.value = _state.value.copy(isValid = true, bankName = bankName, country = "Türkiye")
        } else {
            _state.value = _state.value.copy(isValid = false, error = "IBAN numarası geçersiz (Check-digit hatası).")
        }
    }
}
