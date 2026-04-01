package com.example.nethesap.ui.debt

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

data class DebtState(
    val totalDebt: String = "",
    val interestRate: String = "",
    val monthlyPayment: String = "",
    val extraPayment: String = "",
    val result: DebtResult? = null
)

data class DebtResult(
    val originalMonths: Int,
    val originalTotalInterest: String,
    val simulationMonths: Int,
    val simulationTotalInterest: String,
    val monthsSaved: Int,
    val moneySaved: String,
    val monthlyTable: List<DebtMonth>
)

data class DebtMonth(
    val month: Int,
    val remainingBalance: String,
    val interestPaid: String,
    val principalPaid: String
)

@HiltViewModel
class DebtViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(DebtState())
    val state: State<DebtState> = _state

    private val turkishFormatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("tr", "TR")))

    fun onTotalDebtChange(value: String) {
        _state.value = _state.value.copy(totalDebt = value)
        calculate()
    }

    fun onInterestRateChange(value: String) {
        _state.value = _state.value.copy(interestRate = value)
        calculate()
    }

    fun onMonthlyPaymentChange(value: String) {
        _state.value = _state.value.copy(monthlyPayment = value)
        calculate()
    }

    fun onExtraPaymentChange(value: String) {
        _state.value = _state.value.copy(extraPayment = value)
        calculate()
    }

    private fun calculate() {
        val debt = _state.value.totalDebt.toDoubleOrNull() ?: return
        val annualRate = _state.value.interestRate.toDoubleOrNull() ?: return
        val payment = _state.value.monthlyPayment.toDoubleOrNull() ?: return
        val extra = _state.value.extraPayment.toDoubleOrNull() ?: 0.0

        if (debt <= 0 || annualRate < 0 || payment <= 0) {
            _state.value = _state.value.copy(result = null)
            return
        }

        val monthlyRate = annualRate / 100 / 12
        
        // Original Plan
        val original = simulate(debt, monthlyRate, payment)
        if (original == null) {
            _state.value = _state.value.copy(result = null)
            return
        }

        // Simulation Plan (with extra)
        val simulation = simulate(debt, monthlyRate, payment + extra) ?: original

        _state.value = _state.value.copy(
            result = DebtResult(
                originalMonths = original.first,
                originalTotalInterest = turkishFormatter.format(original.second),
                simulationMonths = simulation.first,
                simulationTotalInterest = turkishFormatter.format(simulation.second),
                monthsSaved = original.first - simulation.first,
                moneySaved = turkishFormatter.format(original.second - simulation.second),
                monthlyTable = simulation.third
            )
        )
    }

    private fun simulate(debt: Double, monthlyRate: Double, totalMonthlyPayment: Double): Triple<Int, Double, List<DebtMonth>>? {
        var remaining = debt
        var totalInterest = 0.0
        var months = 0
        val table = mutableListOf<DebtMonth>()

        // Limit to 600 months (50 years) to prevent infinite loops if payment < interest
        while (remaining > 0.01 && months < 600) {
            val interestPart = remaining * monthlyRate
            if (interestPart >= totalMonthlyPayment) return null // Debt will never be paid

            val principalPart = minOf(remaining, totalMonthlyPayment - interestPart)
            remaining -= principalPart
            totalInterest += interestPart
            months++

            if (months <= 12) { // Only show first 12 months in table for brevity or just first year
                table.add(
                    DebtMonth(
                        month = months,
                        remainingBalance = turkishFormatter.format(remaining),
                        interestPaid = turkishFormatter.format(interestPart),
                        principalPaid = turkishFormatter.format(principalPart)
                    )
                )
            }
        }

        return if (months >= 600) null else Triple(months, totalInterest, table)
    }
}
