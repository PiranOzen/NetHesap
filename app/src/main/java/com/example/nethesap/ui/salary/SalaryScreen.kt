package com.example.nethesap.ui.salary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nethesap.ui.theme.DeepBlue
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryScreen(
    viewModel: SalaryViewModel = hiltViewModel(),
    onMenuClick: () -> Unit
) {
    val state = viewModel.state.value
    val horizontalScrollState = rememberScrollState()
    
    val symbols = DecimalFormatSymbols(Locale("tr", "TR")).apply {
        groupingSeparator = '.'
    }
    val formatter = DecimalFormat("#,###", symbols)

    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(state.amount) {
        val cleanAmount = state.amount.replace(".", "").replace(",", "")
        if (cleanAmount.isEmpty()) {
            textFieldValue = TextFieldValue("")
        } else {
            val formatted = try {
                formatter.format(cleanAmount.toLong())
            } catch (e: Exception) {
                cleanAmount
            }
            if (textFieldValue.text != formatted) {
                textFieldValue = TextFieldValue(text = formatted, selection = TextRange(formatted.length))
            }
        }
    }

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
                        "Detaylı Maaş Hesabı",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TabRow(
                            selectedTabIndex = if (state.isNetToGross) 1 else 0,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[if (state.isNetToGross) 1 else 0]),
                                    color = DeepBlue
                                )
                            }
                        ) {
                            Tab(
                                selected = !state.isNetToGross,
                                onClick = { if (state.isNetToGross) viewModel.toggleCalculationType() },
                                text = { 
                                    Text(
                                        "Brütten Nete",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (!state.isNetToGross) DeepBlue else Color.Black
                                    ) 
                                }
                            )
                            Tab(
                                selected = state.isNetToGross,
                                onClick = { if (!state.isNetToGross) viewModel.toggleCalculationType() },
                                text = { 
                                    Text(
                                        "Netten Brüte",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (state.isNetToGross) DeepBlue else Color.Black
                                    ) 
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = textFieldValue,
                            onValueChange = { newValue ->
                                val cleanString = newValue.text.replace(".", "").replace(",", "")
                                if (cleanString.length <= 12) {
                                    val formatted = if (cleanString.isEmpty()) "" else {
                                        try {
                                            formatter.format(cleanString.toLong())
                                        } catch (e: Exception) {
                                            cleanString
                                        }
                                    }
                                    
                                    if (cleanString.isEmpty() || cleanString.all { it.isDigit() }) {
                                        textFieldValue = newValue.copy(text = formatted, selection = TextRange(formatted.length))
                                        viewModel.onAmountChange(cleanString)
                                    }
                                }
                            },
                            label = { 
                                Text(
                                    if (state.isNetToGross) "Hedef Net Maaş" else "Brüt Maaş",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                ) 
                            },
                            suffix = { 
                                Text(
                                    "₺",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                unfocusedLabelColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        if (state.averageNet > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Payments,
                                            contentDescription = null,
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Ortalama Net Maaş:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                    Text(
                                        "${formatCurrency(state.averageNet)} ₺",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.monthlyResults.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize().horizontalScroll(horizontalScrollState)) {
                    Column {
                        TableHeader()
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(state.monthlyResults) { result ->
                                val isSelected = state.selectedMonth == result.month
                                Row(
                                    modifier = Modifier
                                        .background(if (isSelected) Color(0xFFE3F2FD) else Color.Transparent)
                                        .clickable { viewModel.onMonthClick(result.month) }
                                ) {
                                    TableRow(result)
                                }
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            }
                            state.totalResult?.let {
                                item {
                                    TableFooter(it)
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = DeepBlue.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Hesaplamak için bir tutar giriniz",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Yasal Bilgilendirme: Bu ekranda sunulan veriler ve hesaplamalar yalnızca bilgilendirme amaçlıdır. Verilerin resmi bir geçerliliği veya yasal bağlayıcılığı bulunmamaktadır. En doğru ve kesin sonuçlar için lütfen resmi kurumlara veya mali müşavirinize danışınız.",
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                    color = Color(0xFFBF360C)
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
fun TableHeader() {
    Row(
        modifier = Modifier
            .background(Color(0xFFECEFF1))
            .padding(vertical = 12.dp)
    ) {
        TableCell("Ay", width = 80.dp, isHeader = true)
        TableCell("Net Ele Geçen", isHeader = true, color = DeepBlue)
        TableCell("Brüt", isHeader = true)
        TableCell("SGK İşçi", isHeader = true)
        TableCell("İşsizlik İşçi", isHeader = true)
        TableCell("Gelir Vergisi", isHeader = true)
        TableCell("Damga Vergisi", isHeader = true)
        TableCell("Küm. GV Matrahı", isHeader = true)
        TableCell("İstisna (GV)", isHeader = true)
        TableCell("İstisna (DV)", isHeader = true)
        TableCell("SGK İşveren", isHeader = true)
        TableCell("İşsizlik İşv.", isHeader = true)
        TableCell("Toplam Maliyet", isHeader = true, color = Color(0xFFC62828))
    }
}

@Composable
fun TableRow(result: MonthlySalary) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        TableCell(result.month, width = 80.dp, fontWeight = FontWeight.Bold)
        TableCell(formatCurrency(result.finalNet), fontWeight = FontWeight.Bold, color = DeepBlue)
        TableCell(formatCurrency(result.gross))
        TableCell(formatCurrency(result.sgkWorker))
        TableCell(formatCurrency(result.unemploymentWorker))
        TableCell(formatCurrency(result.incomeTax))
        TableCell(formatCurrency(result.stampTax))
        TableCell(formatCurrency(result.cumulativeIncomeTaxBase))
        TableCell(formatCurrency(result.incomeTaxExemption), color = Color(0xFF2E7D32))
        TableCell(formatCurrency(result.stampTaxExemption), color = Color(0xFF2E7D32))
        TableCell(formatCurrency(result.sgkEmployer))
        TableCell(formatCurrency(result.unemploymentEmployer))
        TableCell(formatCurrency(result.totalCost), fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
    }
}

@Composable
fun TableFooter(total: MonthlySalary) {
    Row(
        modifier = Modifier
            .background(Color(0xFFCFD8DC))
            .padding(vertical = 12.dp)
    ) {
        TableCell("TOPLAM", width = 80.dp, isHeader = true)
        TableCell(formatCurrency(total.finalNet), isHeader = true, color = DeepBlue)
        TableCell(formatCurrency(total.gross), isHeader = true)
        TableCell(formatCurrency(total.sgkWorker), isHeader = true)
        TableCell(formatCurrency(total.unemploymentWorker), isHeader = true)
        TableCell(formatCurrency(total.incomeTax), isHeader = true)
        TableCell(formatCurrency(total.stampTax), isHeader = true)
        TableCell("-", isHeader = true)
        TableCell(formatCurrency(total.incomeTaxExemption), isHeader = true)
        TableCell(formatCurrency(total.stampTaxExemption), isHeader = true)
        TableCell(formatCurrency(total.sgkEmployer), isHeader = true)
        TableCell(formatCurrency(total.unemploymentEmployer), isHeader = true)
        TableCell(formatCurrency(total.totalCost), isHeader = true, color = Color(0xFFC62828))
    }
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp = 110.dp,
    isHeader: Boolean = false,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Unspecified
) {
    Text(
        text = text,
        modifier = Modifier.width(width).padding(horizontal = 4.dp),
        style = if (isHeader) MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold) 
                else MaterialTheme.typography.bodySmall.copy(fontWeight = fontWeight),
        textAlign = TextAlign.End,
        color = color,
        maxLines = 1
    )
}

fun formatCurrency(amount: Double): String {
    return String.format(Locale("tr", "TR"), "%,.2f", amount)
}
