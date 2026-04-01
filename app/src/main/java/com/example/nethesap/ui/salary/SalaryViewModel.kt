package com.example.nethesap.ui.salary

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class SalaryState(
    val amount: String = "",
    val isNetToGross: Boolean = false,
    val monthlyResults: List<MonthlySalary> = emptyList(),
    val totalResult: MonthlySalary? = null,
    val averageNet: Double = 0.0,
    val showDetails: Boolean = false,
    val selectedMonth: String? = null
)

data class MonthlySalary(
    val month: String,
    val gross: Double,
    val net: Double,
    val sgkWorker: Double,
    val unemploymentWorker: Double,
    val incomeTax: Double,
    val stampTax: Double,
    val cumulativeIncomeTaxBase: Double,
    val incomeTaxExemption: Double = 0.0,
    val stampTaxExemption: Double = 0.0,
    val finalNet: Double = 0.0,
    val sgkEmployer: Double = 0.0,
    val unemploymentEmployer: Double = 0.0,
    val totalCost: Double = 0.0
)

@HiltViewModel
class SalaryViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(SalaryState())
    val state: State<SalaryState> = _state

    private val months = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )

    // 2026 Yılı Resmi Verileri
    private val MIN_GROSS = 33030.00 // 2026 Brüt Asgari Ücret
    private val STAMP_TAX_RATE = 0.00759
    private val SGK_WORKER_RATE = 0.14
    private val UNEMPLOYMENT_WORKER_RATE = 0.01
    private val SGK_EMPLOYER_RATE = 0.155
    private val UNEMPLOYMENT_EMPLOYER_RATE = 0.02

    fun onAmountChange(newAmount: String) {
        _state.value = _state.value.copy(amount = newAmount)
        calculate()
    }

    fun onMonthClick(month: String) {
        _state.value = _state.value.copy(
            selectedMonth = if (_state.value.selectedMonth == month) null else month
        )
    }

    fun toggleCalculationType() {
        _state.value = _state.value.copy(isNetToGross = !_state.value.isNetToGross, amount = "", selectedMonth = null)
        calculate()
    }

    fun toggleDetails() {
        _state.value = _state.value.copy(showDetails = !_state.value.showDetails)
    }

    private fun calculate() {
        val inputAmount = _state.value.amount.replace(",", ".").toDoubleOrNull() ?: 0.0
        if (inputAmount <= 0) {
            _state.value = _state.value.copy(monthlyResults = emptyList(), totalResult = null, averageNet = 0.0)
            return
        }

        if (_state.value.isNetToGross) {
            calculateNetToGross(inputAmount)
        } else {
            calculateGrossToNet(inputAmount)
        }
    }

    private fun calculateGrossToNet(grossSalary: Double) {
        val results = mutableListOf<MonthlySalary>()
        var cumulativeBase = 0.0
        var cumulativeMinWageBase = 0.0

        for (monthName in months) {
            val monthlyResult = calculateMonthly(grossSalary, cumulativeBase, cumulativeMinWageBase)
            results.add(monthlyResult.copy(month = monthName))
            
            cumulativeBase += (grossSalary - monthlyResult.sgkWorker - monthlyResult.unemploymentWorker)
            val minWageBase = MIN_GROSS - (MIN_GROSS * SGK_WORKER_RATE) - (MIN_GROSS * UNEMPLOYMENT_WORKER_RATE)
            cumulativeMinWageBase += minWageBase
        }
        
        val total = calculateTotal(results)
        _state.value = _state.value.copy(
            monthlyResults = results,
            totalResult = total,
            averageNet = total.finalNet / 12
        )
    }

    private fun calculateNetToGross(targetNet: Double) {
        val results = mutableListOf<MonthlySalary>()
        var cumulativeBase = 0.0
        var cumulativeMinWageBase = 0.0

        for (monthName in months) {
            var low = targetNet
            var high = targetNet * 5.0
            var estimatedGross = targetNet
            var currentMonthlyResult: MonthlySalary? = null
            
            repeat(30) {
                estimatedGross = (low + high) / 2.0
                val res = calculateMonthly(estimatedGross, cumulativeBase, cumulativeMinWageBase)
                if (res.finalNet < targetNet) low = estimatedGross else high = estimatedGross
                currentMonthlyResult = res
            }

            currentMonthlyResult?.let { res ->
                results.add(res.copy(month = monthName))
                cumulativeBase += (res.gross - res.sgkWorker - res.unemploymentWorker)
                cumulativeMinWageBase += (MIN_GROSS - (MIN_GROSS * SGK_WORKER_RATE) - (MIN_GROSS * UNEMPLOYMENT_WORKER_RATE))
            }
        }
        val total = calculateTotal(results)
        _state.value = _state.value.copy(
            monthlyResults = results,
            totalResult = total,
            averageNet = total.finalNet / 12
        )
    }

    private fun calculateMonthly(gross: Double, cumulativeBase: Double, cumulativeMinWageBase: Double): MonthlySalary {
        val sgkWorker = gross * SGK_WORKER_RATE
        val unemploymentWorker = gross * UNEMPLOYMENT_WORKER_RATE
        val taxBase = gross - sgkWorker - unemploymentWorker
        
        val rawIncomeTax = calculateIncomeTax(taxBase, cumulativeBase)
        
        val minWageTaxBase = MIN_GROSS - (MIN_GROSS * SGK_WORKER_RATE) - (MIN_GROSS * UNEMPLOYMENT_WORKER_RATE)
        val minWageIncomeTax = calculateIncomeTax(minWageTaxBase, cumulativeMinWageBase)
        
        val stampTax = gross * STAMP_TAX_RATE
        val minWageStampTax = MIN_GROSS * STAMP_TAX_RATE
        
        val payableIncomeTax = maxOf(0.0, rawIncomeTax - minWageIncomeTax)
        val payableStampTax = maxOf(0.0, stampTax - minWageStampTax)
        
        val net = gross - sgkWorker - unemploymentWorker - payableIncomeTax - payableStampTax
        
        val sgkEmployer = gross * SGK_EMPLOYER_RATE
        val unemploymentEmployer = gross * UNEMPLOYMENT_EMPLOYER_RATE
        val totalCost = gross + sgkEmployer + unemploymentEmployer

        return MonthlySalary(
            month = "",
            gross = gross,
            net = net,
            sgkWorker = sgkWorker,
            unemploymentWorker = unemploymentWorker,
            incomeTax = payableIncomeTax,
            stampTax = payableStampTax,
            cumulativeIncomeTaxBase = cumulativeBase + taxBase,
            incomeTaxExemption = minWageIncomeTax,
            stampTaxExemption = minWageStampTax,
            finalNet = net,
            sgkEmployer = sgkEmployer,
            unemploymentEmployer = unemploymentEmployer,
            totalCost = totalCost
        )
    }

    private fun calculateIncomeTax(base: Double, cumulativeBase: Double): Double {
        val brackets = listOf(350000.0, 750000.0, 1800000.0, 8000000.0)
        val rates = listOf(0.15, 0.20, 0.27, 0.35, 0.40)
        
        var remainingBase = base
        var currentCumulative = cumulativeBase
        var totalTax = 0.0
        
        for (i in brackets.indices) {
            val bracketLimit = brackets[i]
            if (currentCumulative < bracketLimit) {
                val spaceInBracket = bracketLimit - currentCumulative
                val taxableInThisBracket = minOf(remainingBase, spaceInBracket)
                totalTax += taxableInThisBracket * rates[i]
                remainingBase -= taxableInThisBracket
                currentCumulative += taxableInThisBracket
            }
            if (remainingBase <= 0) break
        }
        if (remainingBase > 0) totalTax += remainingBase * rates.last()
        return totalTax
    }

    private fun calculateTotal(results: List<MonthlySalary>): MonthlySalary {
        return MonthlySalary(
            month = "TOPLAM",
            gross = results.sumOf { it.gross },
            net = results.sumOf { it.net },
            sgkWorker = results.sumOf { it.sgkWorker },
            unemploymentWorker = results.sumOf { it.unemploymentWorker },
            incomeTax = results.sumOf { it.incomeTax },
            stampTax = results.sumOf { it.stampTax },
            cumulativeIncomeTaxBase = results.last().cumulativeIncomeTaxBase,
            incomeTaxExemption = results.sumOf { it.incomeTaxExemption },
            stampTaxExemption = results.sumOf { it.stampTaxExemption },
            finalNet = results.sumOf { it.finalNet },
            sgkEmployer = results.sumOf { it.sgkEmployer },
            unemploymentEmployer = results.sumOf { it.unemploymentEmployer },
            totalCost = results.sumOf { it.totalCost }
        )
    }
}
