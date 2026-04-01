package com.example.nethesap.ui.calculator

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: String? = null,
    val displayText: String = "0",
    val isResultDisplayed: Boolean = false // Sonuç gösteriliyor mu takibi
)

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(CalculatorState())
    val state: State<CalculatorState> = _state

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Clear -> _state.value = CalculatorState()
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.Decimal -> enterDecimal()
        }
    }

    private fun enterNumber(number: Int) {
        // Eğer bir sonuç gösteriliyorsa ve yeni sayıya basıldıysa, her şeyi sıfırla ve yeni sayıyı yaz
        if (_state.value.isResultDisplayed && _state.value.operation == null) {
            _state.value = CalculatorState(
                number1 = number.toString(),
                displayText = number.toString()
            )
            return
        }

        if (_state.value.operation == null) {
            if (_state.value.number1.length >= 10) return
            _state.value = _state.value.copy(
                number1 = _state.value.number1 + number,
                displayText = _state.value.number1 + number
            )
        } else {
            if (_state.value.number2.length >= 10) return
            _state.value = _state.value.copy(
                number2 = _state.value.number2 + number,
                displayText = _state.value.number2 + number
            )
        }
    }

    private fun enterOperation(operation: String) {
        if (_state.value.number1.isNotBlank()) {
            _state.value = _state.value.copy(
                operation = operation,
                isResultDisplayed = false // İşlem seçildiğinde artık sonuç modunda değiliz
            )
        }
    }

    private fun enterDecimal() {
        if (_state.value.isResultDisplayed && _state.value.operation == null) {
            _state.value = CalculatorState(number1 = "0.", displayText = "0.")
            return
        }

        if (_state.value.operation == null && !_state.value.number1.contains(".") && _state.value.number1.isNotBlank()) {
            _state.value = _state.value.copy(number1 = _state.value.number1 + ".", displayText = _state.value.number1 + ".")
        } else if (!_state.value.number2.contains(".") && _state.value.number2.isNotBlank()) {
            _state.value = _state.value.copy(number2 = _state.value.number2 + ".", displayText = _state.value.number2 + ".")
        }
    }

    private fun delete() {
        if (_state.value.operation == null && _state.value.number1.isNotBlank()) {
            _state.value = _state.value.copy(
                number1 = _state.value.number1.dropLast(1),
                displayText = if (_state.value.number1.length > 1) _state.value.number1.dropLast(1) else "0"
            )
        } else if (_state.value.operation != null && _state.value.number2.isNotBlank()) {
            _state.value = _state.value.copy(
                number2 = _state.value.number2.dropLast(1),
                displayText = if (_state.value.number2.length > 1) _state.value.number2.dropLast(1) else "0"
            )
        } else if (_state.value.operation != null && _state.value.number2.isEmpty()) {
            _state.value = _state.value.copy(operation = null, displayText = _state.value.number1)
        }
    }

    private fun performCalculation() {
        val number1 = _state.value.number1.toDoubleOrNull()
        val number2 = _state.value.number2.toDoubleOrNull()
        if (number1 != null && number2 != null) {
            val result = when (_state.value.operation) {
                "+" -> number1 + number2
                "-" -> number1 - number2
                "*" -> number1 * number2
                "/" -> number1 / number2
                else -> return
            }
            val resultStr = if (result % 1 == 0.0) result.toLong().toString() else result.toString()
            _state.value = CalculatorState(
                number1 = resultStr.take(15),
                displayText = resultStr.take(15),
                isResultDisplayed = true // Hesaplama yapıldı, sonuç gösteriliyor
            )
        }
    }
}

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    object Calculate : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
}
