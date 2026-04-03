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
    val isResultDisplayed: Boolean = false,
    val error: String? = null
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
        if (_state.value.isResultDisplayed && _state.value.operation == null) {
            _state.value = CalculatorState(
                number1 = number.toString(),
                displayText = number.toString()
            )
            return
        }

        if (_state.value.operation == null) {
            if (_state.value.number1.length >= 10) return
            val newNumber = _state.value.number1 + number
            _state.value = _state.value.copy(
                number1 = newNumber,
                displayText = newNumber,
                error = null
            )
        } else {
            if (_state.value.number2.length >= 10) return
            val newNumber = _state.value.number2 + number
            _state.value = _state.value.copy(
                number2 = newNumber,
                displayText = newNumber,
                error = null
            )
        }
    }

    private fun enterOperation(operation: String) {
        if (_state.value.number1.isNotBlank()) {
            _state.value = _state.value.copy(
                operation = operation,
                isResultDisplayed = false,
                error = null
            )
        }
    }

    private fun enterDecimal() {
        if (_state.value.isResultDisplayed && _state.value.operation == null) {
            _state.value = CalculatorState(number1 = "0.", displayText = "0.")
            return
        }

        if (_state.value.operation == null) {
            if (!_state.value.number1.contains(".") && _state.value.number1.isNotBlank()) {
                _state.value = _state.value.copy(number1 = _state.value.number1 + ".", displayText = _state.value.number1 + ".")
            } else if (_state.value.number1.isEmpty()) {
                _state.value = _state.value.copy(number1 = "0.", displayText = "0.")
            }
        } else {
            if (!_state.value.number2.contains(".") && _state.value.number2.isNotBlank()) {
                _state.value = _state.value.copy(number2 = _state.value.number2 + ".", displayText = _state.value.number2 + ".")
            } else if (_state.value.number2.isEmpty()) {
                _state.value = _state.value.copy(number2 = "0.", displayText = "0.")
            }
        }
    }

    private fun delete() {
        if (_state.value.operation == null && _state.value.number1.isNotBlank()) {
            val newNumber = _state.value.number1.dropLast(1)
            _state.value = _state.value.copy(
                number1 = newNumber,
                displayText = if (newNumber.isEmpty()) "0" else newNumber
            )
        } else if (_state.value.operation != null && _state.value.number2.isNotBlank()) {
            val newNumber = _state.value.number2.dropLast(1)
            _state.value = _state.value.copy(
                number2 = newNumber,
                displayText = if (newNumber.isEmpty()) "0" else newNumber
            )
        } else if (_state.value.operation != null && _state.value.number2.isEmpty()) {
            _state.value = _state.value.copy(operation = null, displayText = _state.value.number1)
        }
    }

    private fun performCalculation() {
        val number1 = _state.value.number1.toDoubleOrNull()
        val number2 = _state.value.number2.toDoubleOrNull()
        
        if (number1 != null && number2 != null) {
            if (_state.value.operation == "/" && number2 == 0.0) {
                _state.value = _state.value.copy(
                    displayText = "Hata",
                    error = "Sıfıra bölünemez",
                    isResultDisplayed = true
                )
                return
            }

            val result = try {
                when (_state.value.operation) {
                    "+" -> number1 + number2
                    "-" -> number1 - number2
                    "*" -> number1 * number2
                    "/" -> number1 / number2
                    else -> null
                }
            } catch (e: Exception) {
                null
            }

            if (result != null) {
                val resultStr = if (result.isInfinite() || result.isNaN()) {
                    "Hata"
                } else if (result % 1 == 0.0) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }

                _state.value = CalculatorState(
                    number1 = resultStr.take(20),
                    displayText = resultStr.take(20),
                    isResultDisplayed = true
                )
            }
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
