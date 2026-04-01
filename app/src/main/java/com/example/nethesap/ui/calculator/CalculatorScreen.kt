package com.example.nethesap.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nethesap.ui.theme.DeepBlue
import com.example.nethesap.ui.theme.LightBlue

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel(),
    onMenuClick: () -> Unit
) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(DeepBlue, DeepBlue.copy(alpha = 0.8f))
                        )
                    )
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Hesap Makinesi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Display Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    val operationDisplay = when(state.operation) {
                        "/" -> " ÷ "
                        "*" -> " x "
                        else -> " ${state.operation ?: ""} "
                    }
                    
                    Text(
                        text = state.number1 + (if(state.operation != null) operationDisplay else "") + state.number2,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = state.displayText,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp
                        ),
                        color = DeepBlue,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
            }

            // Buttons Area
            Column(
                modifier = Modifier.weight(2.5f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val rows = listOf(
                    listOf("AC", "SİL", "÷", "x"),
                    listOf("7", "8", "9", "-"),
                    listOf("4", "5", "6", "+"),
                    listOf("1", "2", "3", "="),
                    listOf("0", ".")
                )

                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { symbol ->
                            val isOperator = symbol in listOf("÷", "x", "-", "+", "=", "AC", "SİL")
                            val isSpecial = symbol == "="
                            
                            CalculatorButton(
                                symbol = symbol,
                                modifier = Modifier
                                    .weight(if (symbol == "0") 2.12f else 1f)
                                    .aspectRatio(if (symbol == "0") 2.12f else 1f),
                                color = when {
                                    isSpecial -> DeepBlue
                                    isOperator -> LightBlue
                                    else -> Color.White
                                },
                                contentColor = when {
                                    isSpecial -> Color.White
                                    isOperator -> DeepBlue
                                    else -> Color.Black
                                },
                                onClick = {
                                    when (symbol) {
                                        "AC" -> viewModel.onAction(CalculatorAction.Clear)
                                        "SİL" -> viewModel.onAction(CalculatorAction.Delete)
                                        "=" -> viewModel.onAction(CalculatorAction.Calculate)
                                        "÷" -> viewModel.onAction(CalculatorAction.Operation("/"))
                                        "x" -> viewModel.onAction(CalculatorAction.Operation("*"))
                                        "-" -> viewModel.onAction(CalculatorAction.Operation("-"))
                                        "+" -> viewModel.onAction(CalculatorAction.Operation("+"))
                                        "." -> viewModel.onAction(CalculatorAction.Decimal)
                                        else -> viewModel.onAction(CalculatorAction.Number(symbol.toInt()))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    contentColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (symbol == "SİL") 18.sp else 24.sp
            ),
            color = contentColor
        )
    }
}
