package com.example.nethesap.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nethesap.ui.theme.DeepBlue

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val description: String,
    val route: String
)

@Composable
fun MainScreen(
    onNavigate: (String) -> Unit
) {
    val menuItems = listOf(
        MenuItem("Döviz Kurları", Icons.Default.CurrencyLira, Color(0xFF43A047), "Güncel kur takibi ve çevirici", "currency"),
        MenuItem("Hesap Makinesi", Icons.Default.Calculate, Color(0xFF1E88E5), "Gelişmiş finansal hesaplamalar", "calculator"),
        MenuItem("Maaş Hesaplama", Icons.Default.Payments, Color(0xFFFB8C00), "Net-Brüt maaş ve kesintiler", "salary"),
        MenuItem("Faiz Hesaplama", Icons.Default.Percent, Color(0xFFE53935), "Mevduat ve kredi faizleri", "interest"),
        MenuItem("Borç Kapatma", Icons.Default.Speed, Color(0xFF607D8B), "Borç bitirme simülasyonu", "debt"),
        MenuItem("KDV Hesaplama", Icons.Default.ReceiptLong, Color(0xFF00ACC1), "KDV dahil ve hariç hesaplama", "kdv"),
        MenuItem("Kredi Ödeme", Icons.Default.AccountBalance, Color(0xFF5E35B1), "Taksit ve ödeme planı dökümü", "loan"),
        MenuItem("Yakıt Hesaplama", Icons.Default.LocalGasStation, Color(0xFF7CB342), "Yolculuk maliyet hesaplayıcı", "fuel"),
        MenuItem("İndirim Hesabı", Icons.Default.LocalOffer, Color(0xFFD81B60), "İndirimli fiyat ve kar/zarar", "discount"),
        MenuItem("IBAN Doğrulama", Icons.Default.CreditCard, Color(0xFF3949AB), "IBAN kontrol ve banka bulma", "iban"),
        MenuItem("Vergi Takvimi", Icons.Default.EventNote, Color(0xFF546E7A), "Önemli vergi ödeme günleri", "tax_calendar"),
        MenuItem("Birim Dönüştürücü", Icons.Default.Scale, Color(0xFF8E24AA), "Ölçü birimleri arası dönüşüm", "unit")
    )

    Scaffold(
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(DeepBlue, DeepBlue.copy(alpha = 0.8f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Text(
                        text = "Net Hesap",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Finansal dünyanız kontrol altında",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = 20.dp, y = 20.dp)
                )
            }

            // Menü Izgarası
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuItems) { item ->
                    MenuCard(
                        item = item,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuCard(item: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1A1A1A),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = Color.Gray,
                    lineHeight = 13.sp,
                    maxLines = 2
                )
            }
        }
    }
}
