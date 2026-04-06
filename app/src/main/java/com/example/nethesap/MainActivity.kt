package com.example.nethesap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nethesap.ui.calculator.CalculatorScreen
import com.example.nethesap.ui.components.BannerAdView
import com.example.nethesap.ui.currency.CurrencyScreen
import com.example.nethesap.ui.interest.InterestScreen
import com.example.nethesap.ui.unit.UnitConverterScreen
import com.example.nethesap.ui.main.AdManager
import com.example.nethesap.ui.main.MainScreen
import com.example.nethesap.ui.salary.SalaryScreen
import com.example.nethesap.ui.kdv.KdvScreen
import com.example.nethesap.ui.loan.LoanScreen
import com.example.nethesap.ui.crypto.CryptoScreen
import com.example.nethesap.ui.fuel.FuelScreen
import com.example.nethesap.ui.discount.DiscountScreen
import com.example.nethesap.ui.iban.IbanScreen
import com.example.nethesap.ui.tax.TaxCalendarScreen
import com.example.nethesap.ui.debt.DebtScreen
import com.example.nethesap.ui.theme.NetHesapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Load interstitial ad
        AdManager.loadInterstitialAd(this)

        setContent {
            NetHesapTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            MainScreen(
                                onNavigate = { route ->
                                    scope.launch { drawerState.close() }
                                    // Use the new counter-based interstitial logic
                                    AdManager.showInterstitialWithCounter(this@MainActivity) {
                                        navController.navigate(route) {
                                            if (route == "currency") {
                                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) {
                    Column {
                        Surface(modifier = Modifier.weight(1f)) {
                            NavHost(
                                navController = navController,
                                startDestination = "currency"
                            ) {
                                composable("currency") {
                                    CurrencyScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("calculator") {
                                    CalculatorScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("interest") {
                                    InterestScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("unit") {
                                    UnitConverterScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("salary") {
                                    SalaryScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("kdv") {
                                    KdvScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("loan") {
                                    LoanScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("crypto") {
                                    CryptoScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("fuel") {
                                    FuelScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("discount") {
                                    DiscountScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("iban") {
                                    IbanScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("tax_calendar") {
                                    TaxCalendarScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                                composable("debt") {
                                    DebtScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                            }
                        }
                        // Banner ad at the bottom
                        BannerAdView(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}
