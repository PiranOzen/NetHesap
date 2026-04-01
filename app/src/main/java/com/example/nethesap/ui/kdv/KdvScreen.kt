package com.example.nethesap.ui.kdv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun KdvScreen(
    viewModel: KdvViewModel = hiltViewModel(),
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
                        "KDV Hesaplama",
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Hesaplama Türü", fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = !state.isKdvIncludedInput,
                            onClick = { viewModel.onInputTypeChange(false) },
                            colors = RadioButtonDefaults.colors(selectedColor = DeepBlue)
                        )
                        Text("KDV Hariç Tutar", color = Color.Black, modifier = Modifier.clickable { viewModel.onInputTypeChange(false) })
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = state.isKdvIncludedInput,
                            onClick = { viewModel.onInputTypeChange(true) },
                            colors = RadioButtonDefaults.colors(selectedColor = DeepBlue)
                        )
                        Text("KDV Dahil Tutar", color = Color.Black, modifier = Modifier.clickable { viewModel.onInputTypeChange(true) })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = { viewModel.onAmountChange(it) },
                        label = { Text("Tutar", fontWeight = FontWeight.Bold, color = Color.Black) },
                        suffix = { Text("₺", fontWeight = FontWeight.Bold, color = Color.Black) },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("KDV Oranı", fontWeight = FontWeight.Bold, color = Color.Black)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1.0, 10.0, 20.0).forEach { rate ->
                            FilterChip(
                                selected = state.kdvRate == rate,
                                onClick = { viewModel.onRateChange(rate) },
                                label = { Text("%${rate.toInt()}", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResultRow("KDV Hariç Tutar", state.kdvExcluded)
                    ResultRow("KDV Tutarı (%${state.kdvRate.toInt()})", state.kdvAmount)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    ResultRow("KDV Dahil TOPLAM", state.kdvIncluded, isTotal = true)
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: Double, isTotal: Boolean = false) {
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
