package com.example.nethesap.ui.tax

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nethesap.ui.theme.DeepBlue

data class TaxEvent(
    val date: String,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxCalendarScreen(
    onMenuClick: () -> Unit
) {
    val taxEvents = listOf(
        TaxEvent("26 Ocak", "KDV Beyannamesi", "Aralık ayı Katma Değer Vergisi beyannamesinin verilmesi ve ödenmesi."),
        TaxEvent("26 Ocak", "Muhtasar Beyanname", "Aralık ayı Muhtasar ve Prim Hizmet Beyannamesinin verilmesi ve ödenmesi."),
        TaxEvent("31 Ocak", "Motorlu Taşıtlar Vergisi", "MTV 1. Taksit ödemesi için son gün."),
        TaxEvent("26 Şubat", "Gelir Geçici Vergi", "Ekim-Kasım-Aralık dönemi Gelir Geçici Vergisinin beyanı ve ödenmesi."),
        TaxEvent("31 Mart", "Gelir Vergisi", "Yıllık Gelir Vergisi beyannamesinin verilmesi ve 1. taksit ödemesi."),
        TaxEvent("30 Nisan", "Kurumlar Vergisi", "Yıllık Kurumlar Vergisi beyannamesinin verilmesi ve ödenmesi."),
        TaxEvent("31 Mayıs", "Emlak Vergisi", "Emlak Vergisi 1. Taksit ödemesi için son gün."),
        TaxEvent("31 Temmuz", "Motorlu Taşıtlar Vergisi", "MTV 2. Taksit ödemesi için son gün."),
        TaxEvent("30 Kasım", "Emlak Vergisi", "Emlak Vergisi 2. Taksit ödemesi için son gün.")
    )

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
                        "Vergi Takvimi",
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
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(taxEvents) { event ->
                TaxEventCard(event)
            }
        }
    }
}

@Composable
fun TaxEventCard(event: TaxEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(DeepBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Event, null, tint = DeepBlue)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = event.date,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = DeepBlue
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
