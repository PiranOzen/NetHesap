package com.example.nethesap.ui.debt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(
    viewModel: DebtViewModel = hiltViewModel(),
    onMenuClick: () -> Unit
) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            @Suppress("DEPRECATION")
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
                        "Borç Kapatma Simülasyonu",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DebtInputField(
                            value = state.totalDebt,
                            onValueChange = { viewModel.onTotalDebtChange(it) },
                            label = "Toplam Borç Tutarı",
                            suffix = "₺"
                        )
                        DebtInputField(
                            value = state.interestRate,
                            onValueChange = { viewModel.onInterestRateChange(it) },
                            label = "Yıllık Faiz Oranı",
                            suffix = "%"
                        )
                        DebtInputField(
                            value = state.monthlyPayment,
                            onValueChange = { viewModel.onMonthlyPaymentChange(it) },
                            label = "Mevcut Aylık Ödeme",
                            suffix = "₺"
                        )
                        DebtInputField(
                            value = state.extraPayment,
                            onValueChange = { viewModel.onExtraPaymentChange(it) },
                            label = "Ek Ödeme (Aylık Kar Topu)",
                            suffix = "₺"
                        )
                    }
                }
            }

            state.result?.let { result ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepBlue)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simülasyon Özeti", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            
                            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                            
                            ResultRow("Normal Ödeme Süresi", "${result.originalMonths} Ay", Color.White.copy(alpha = 0.7f))
                            ResultRow("Hızlandırılmış Süre", "${result.simulationMonths} Ay", Color.White)
                            
                            Surface(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Borcunuzu ${result.monthsSaved} AY daha erken kapatacaksın!",
                                    modifier = Modifier.padding(12.dp),
                                    color = Color(0xFF81C784),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            
                            ResultRow("Faizden Tasarruf", "${result.moneySaved} ₺", Color(0xFF81C784))
                        }
                    }
                }

                item {
                    Text("İlk 12 Aylık Tahmini Plan", fontWeight = FontWeight.Bold, color = DeepBlue, modifier = Modifier.padding(top = 8.dp))
                }

                items(result.monthlyTable) { month ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${month.month}. Ay", fontWeight = FontWeight.Bold, color = DeepBlue)
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Kalan: ${month.remainingBalance} ₺", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Anapara: ${month.principalPaid} ₺ | Faiz: ${month.interestPaid} ₺", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            } ?: item {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("Hesaplamak için bilgileri giriniz", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DebtInputField(
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
fun ResultRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = color.copy(alpha = 0.8f))
        Text(value, color = color, fontWeight = FontWeight.Bold)
    }
}
