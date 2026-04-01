package com.example.nethesap.ui.loan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nethesap.ui.theme.DeepBlue
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanScreen(
    viewModel: LoanViewModel = hiltViewModel(),
    onMenuClick: () -> Unit
) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.verticalGradient(colors = listOf(DeepBlue, DeepBlue.copy(alpha = 0.8f))))
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                    }
                    Text(
                        "Kredi Hesaplama",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    LoanInputField(
                        value = state.amount,
                        onValueChange = { viewModel.onAmountChange(it) },
                        label = "Kredi Tutarı",
                        suffix = "₺"
                    )
                    LoanInputField(
                        value = state.interestRate,
                        onValueChange = { viewModel.onInterestChange(it) },
                        label = "Aylık Faiz Oranı",
                        suffix = "%"
                    )
                    LoanInputField(
                        value = state.month,
                        onValueChange = { viewModel.onMonthChange(it) },
                        label = "Vade (Ay)"
                    )
                }
            }

            if (state.monthlyPayment > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ResultRow("Aylık Taksit", state.monthlyPayment, isTotal = true)
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        ResultRow("Toplam Geri Ödeme", state.totalPayment)
                        ResultRow("Toplam Faiz", state.totalInterest)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoanInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontWeight = FontWeight.Bold, color = Color.Black) },
        suffix = suffix?.let { { Text(it, fontWeight = FontWeight.Bold, color = Color.Black) } },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

@Composable
private fun ResultRow(label: String, value: Double, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = if (isTotal) DeepBlue else Color.Gray, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(
            String.format(Locale("tr", "TR"), "%,.2f ₺", value),
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (isTotal) 20.sp else 16.sp,
            color = if (isTotal) DeepBlue else Color.Black
        )
    }
}
