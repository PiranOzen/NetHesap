package com.example.nethesap.ui.interest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nethesap.ui.theme.DeepBlue
import com.example.nethesap.ui.theme.LightBlue

@Composable
fun InterestScreen(
    viewModel: InterestViewModel = hiltViewModel(),
    onMenuClick: () -> Unit
) {
    val state = viewModel.state.value
    val scrollState = rememberScrollState()

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
                        text = "Faiz Hesaplama",
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Giriş Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InterestInputField(
                        value = state.principal,
                        onValueChange = { viewModel.onPrincipalChange(it) },
                        label = "Ana Para",
                        placeholder = "Örn: 100.000",
                        suffix = "₺"
                    )

                    InterestInputField(
                        value = state.interestRate,
                        onValueChange = { viewModel.onInterestRateChange(it) },
                        label = "Yıllık Faiz Oranı",
                        placeholder = "Örn: 45",
                        suffix = "%"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        InterestInputField(
                            value = state.duration,
                            onValueChange = { viewModel.onDurationChange(it) },
                            label = "Vade",
                            placeholder = "Örn: 32",
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Vade Tipi Seçimi
                        Row(
                            modifier = Modifier
                                .weight(1.5f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LightBlue)
                                .padding(4.dp)
                        ) {
                            DurationType.values().forEach { type ->
                                val isSelected = state.durationType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DeepBlue else Color.Transparent)
                                        .clickable { viewModel.onDurationTypeChange(type) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when(type) {
                                            DurationType.DAY -> "Gün"
                                            DurationType.MONTH -> "Ay"
                                            DurationType.YEAR -> "Yıl"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else DeepBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sonuç Kartı
            state.result?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ResultRow("Vade Sonu Toplam", "${result.totalAmount} ₺", isLarge = true)
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        ResultRow("Net Faiz Kazancı", "${result.netInterest} ₺")
                        ResultRow("Brüt Faiz", "${result.totalInterest} ₺")
                        ResultRow("Stopaj Kesintisi (%5)", "${result.taxAmount} ₺")
                    }
                }
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hesaplamak için bilgileri girin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, isLarge: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isLarge) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = if (isLarge) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold) 
                    else MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

@Composable
fun InterestInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    suffix: String? = null,
    modifier: Modifier = Modifier
) {
    // İmleç pozisyonunu korumak için TextFieldValue kullanıyoruz
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }

    // Dışarıdan gelen (ViewModel) değer değiştiğinde imleci koruyarak güncelle
    LaunchedEffect(value) {
        if (value != textFieldValueState.text) {
            val oldText = textFieldValueState.text
            val oldSelection = textFieldValueState.selection.end
            
            // İmleçten önceki rakam sayısını bul
            val digitsBeforeCursor = oldText.take(oldSelection).count { it.isDigit() || it == ',' }
            
            // Yeni metinde aynı sayıdaki rakamın pozisyonunu bul
            var newSelection = 0
            var digitsFound = 0
            for (char in value) {
                if (digitsFound == digitsBeforeCursor) break
                if (char.isDigit() || char == ',') digitsFound++
                newSelection++
            }
            
            textFieldValueState = textFieldValueState.copy(
                text = value,
                selection = TextRange(newSelection.coerceIn(0, value.length))
            )
        }
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = textFieldValueState,
            onValueChange = {
                textFieldValueState = it
                onValueChange(it.text)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray.copy(alpha = 0.5f)) },
            suffix = suffix?.let { { Text(it, color = Color.Black, fontWeight = FontWeight.Bold) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DeepBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
    }
}
