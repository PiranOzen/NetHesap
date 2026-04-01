package com.example.nethesap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nethesap.ui.calculator.CalculatorScreen
import com.example.nethesap.ui.currency.CurrencyScreen
import com.example.nethesap.ui.interest.InterestScreen
import com.example.nethesap.ui.unit.UnitConverterScreen
import com.example.nethesap.ui.gold.GoldScreen
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
                                    navController.navigate(route) {
                                        if (route == "currency") {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) {
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
                        composable("gold") {
                            GoldScreen(onMenuClick = { scope.launch { drawerState.open() } })
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
            }
        }
    }
}
