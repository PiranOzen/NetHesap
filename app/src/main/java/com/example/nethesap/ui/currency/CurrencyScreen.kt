package com.example.nethesap.ui.currency

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
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
import com.example.nethesap.domain.model.Currency
import com.example.nethesap.ui.theme.DeepBlue
import com.example.nethesap.ui.theme.LightBlue

@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrencyViewModel = hiltViewModel(),
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
                // Header Content
                Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onMenuClick,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = "Hoş Geldiniz",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Net Hesap",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                        IconButton(
                            onClick = { viewModel.getCurrencies() },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Piyasa Özeti",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "TCMB Güncel Kurlar",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                
                // Sticky Converter Card inside TopBar
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    ConverterCard(state, viewModel)
                }
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = DeepBlue
                )
            }

            if (state.error.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Popüler Kurlar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 0.dp),
                        color = DeepBlue
                    )
                }

                val mainCurrencies = state.currencies.filter { it.code in listOf("USD", "EUR", "GBP") }
                items(mainCurrencies) { currency ->
                    CurrencyCard(currency, isSelected = state.selectedCurrency?.code == currency.code) {
                        viewModel.onCurrencySelect(currency)
                    }
                }

                item {
                    Text(
                        text = "Diğer Kurlar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        color = DeepBlue
                    )
                }

                val otherCurrencies = state.currencies.filter { it.code !in listOf("USD", "EUR", "GBP") }
                items(otherCurrencies) { currency ->
                    CurrencyListItem(
                        currency = currency,
                        isSelected = state.selectedCurrency?.code == currency.code
                    ) {
                        viewModel.onCurrencySelect(currency)
                    }
                }
            }
        }
    }
}

@Composable
fun ConverterCard(state: CurrencyState, viewModel: CurrencyViewModel) {
    var isFocused by remember { mutableStateOf(false) }
    
    // İmleç pozisyonunu korumak için TextFieldValue kullanıyoruz
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = state.amount)) }

    // ViewModel'den gelen değer değiştiğinde imleci koruyarak güncelle
    LaunchedEffect(state.amount) {
        if (state.amount != textFieldValueState.text) {
            val oldText = textFieldValueState.text
            val oldSelection = textFieldValueState.selection.end
            
            val digitsBeforeCursor = oldText.take(oldSelection).count { it.isDigit() || it == ',' }
            
            var newSelection = 0
            var digitsFound = 0
            for (char in state.amount) {
                if (digitsFound == digitsBeforeCursor) break
                if (char.isDigit() || char == ',') digitsFound++
                newSelection++
            }
            
            textFieldValueState = textFieldValueState.copy(
                text = state.amount,
                selection = TextRange(newSelection.coerceIn(0, state.amount.length))
            )
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hızlı Çevirici",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = DeepBlue,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Source
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (state.isTlrToSelected) "TRY" else state.selectedCurrency?.code ?: "Seçiniz",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = if (!isFocused && textFieldValueState.text == "0") textFieldValueState.copy(text = "") else textFieldValueState,
                        onValueChange = { 
                            textFieldValueState = it
                            viewModel.onAmountChange(it.text) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                if (focusState.isFocused && state.amount == "100") {
                                    viewModel.onAmountChange("")
                                } else if (!focusState.isFocused && state.amount.isEmpty()) {
                                    viewModel.onAmountChange("0")
                                }
                            },
                        placeholder = { Text("Miktar girin") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A)
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = Color(0xFF1A1A1A)
                        )
                    )
                }

                IconButton(
                    onClick = { viewModel.onToggleDirection() },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 16.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.SyncAlt,
                        contentDescription = "Switch",
                        tint = DeepBlue
                    )
                }

                // Target
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (state.isTlrToSelected) state.selectedCurrency?.code ?: "Seçiniz" else "TRY",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF8F9FA))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = state.result,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = DeepBlue
                        )
                    }
                }
            }
            
            if (state.selectedCurrency != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "1 ${state.selectedCurrency.code} = ${if(state.isTlrToSelected) state.selectedCurrency.sellingPrice else state.selectedCurrency.buyingPrice} TRY",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CurrencyCard(currency: Currency, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        border = if (isSelected) BorderStroke(1.dp, DeepBlue) else null
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) DeepBlue else LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currency.code.take(1),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Color.White else DeepBlue
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = currency.code,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Text(
                        text = currency.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${currency.sellingPrice} ₺",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = DeepBlue
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF00C853),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Alış: ${currency.buyingPrice}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF00C853)
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyListItem(
    currency: Currency,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .alpha(if (isSelected) 1f else 0.7f),
        color = if (isSelected) Color(0xFFE3F2FD) else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(1.dp, DeepBlue) else null,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                    ),
                    modifier = Modifier.width(45.dp),
                    color = if (isSelected) DeepBlue else Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) DeepBlue.copy(alpha = 0.8f) else Color.Gray,
                    maxLines = 1
                )
            }
            
            Row {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 16.dp)) {
                    Text("Alış", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = currency.buyingPrice,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = if (isSelected) Color(0xFF00796B) else Color.Black
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Satış", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = currency.sellingPrice,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = if (isSelected) DeepBlue else DeepBlue
                    )
                }
            }
        }
    }
}
