package com.hubenko.firestoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.feature.admin.ui.AdminScreen
import com.hubenko.feature.admin.ui.register.RegisterEmployeeScreen
import com.hubenko.feature.admin.ui.reminder.ReminderSettingsScreen
import com.hubenko.feature.auth.ui.AuthScreen
import com.hubenko.feature.home.ui.HomeScreen
import com.hubenko.feature.status.ui.StatusScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
                    val startDestination = if (isUserLoggedIn) "home" else "login"

                    NavHost(navController = navController, startDestination = startDestination) {
                        
                        composable("login") {
                            AuthScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onNavigateToStatus = { navController.navigate("status") },
                                onNavigateToAdmin = { navController.navigate("admin") },
                                onNavigateToAuth = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("status") {
                            StatusScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("admin") {
                            AdminScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToReminderSettings = { employeeId ->
                                    navController.navigate("reminder_settings/$employeeId")
                                },
                                onNavigateToRegisterEmployee = {
                                    navController.navigate("admin_register")
                                }
                            )
                        }

                        composable("admin_register") {
                            RegisterEmployeeScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("reminder_settings/{employeeId}") { backStackEntry ->
                            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
                            ReminderSettingsScreen(
                                employeeId = employeeId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
