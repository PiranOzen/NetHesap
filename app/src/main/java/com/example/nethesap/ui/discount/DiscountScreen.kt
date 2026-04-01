package com.example.nethesap.ui.discount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountScreen(
    viewModel: DiscountViewModel = hiltViewModel(),
    onMenuClick: () -> Unit
) {
    val state = viewModel.state.value
    val scrollState = rememberScrollState()

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
                        "İndirim & Kar Hesaplama",
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
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // İndirim Hesaplama Bölümü
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("İndirim Hesaplama", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DeepBlue)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DiscountInputField(
                            value = state.price,
                            onValueChange = { viewModel.onPriceChange(it) },
                            label = "Etiket Fiyatı",
                            suffix = "₺"
                        )
                        DiscountInputField(
                            value = state.discountRate,
                            onValueChange = { viewModel.onDiscountRateChange(it) },
                            label = "İndirim Oranı",
                            suffix = "%"
                        )
                        
                        if (state.discountedPrice > 0) {
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            ResultRow("İndirimli Fiyat", state.discountedPrice, isTotal = true)
                            ResultRow("Kazancınız", state.savingAmount)
                        }
                    }
                }
            }

            // Kar/Zarar Hesaplama Bölümü
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Kar / Zarar Hesaplama", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DeepBlue)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DiscountInputField(
                            value = state.cost,
                            onValueChange = { viewModel.onCostChange(it) },
                            label = "Alış Fiyatı (Maliyet)",
                            suffix = "₺"
                        )
                        DiscountInputField(
                            value = state.sellPrice,
                            onValueChange = { viewModel.onSellPriceChange(it) },
                            label = "Satış Fiyatı",
                            suffix = "₺"
                        )
                        
                        if (state.profitAmount != 0.0) {
                            val isProfit = state.profitAmount > 0
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            ResultRow(
                                if (isProfit) "Kar Tutarı" else "Zarar Tutarı",
                                Math.abs(state.profitAmount),
                                isTotal = true,
                                customColor = if (isProfit) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Oran", color = Color.Gray)
                                Text(
                                    String.format(Locale("tr", "TR"), "%% %.2f", Math.abs(state.profitRate)),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isProfit) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscountInputField(
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
private fun ResultRow(label: String, value: Double, isTotal: Boolean = false, customColor: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val displayColor = customColor ?: if (isTotal) DeepBlue else Color.Gray
        Text(label, color = displayColor, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(
            String.format(Locale("tr", "TR"), "%,.2f ₺", value),
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (isTotal) 20.sp else 16.sp,
            color = displayColor
        )
    }
}
