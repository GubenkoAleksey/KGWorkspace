package com.hubenko.firestoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.navigation.AdminRoute
import com.hubenko.feature.admin.navigation.adminGraph
import com.hubenko.feature.auth.navigation.AuthRoute
import com.hubenko.feature.auth.navigation.authGraph
import com.hubenko.feature.home.navigation.HomeRoute
import com.hubenko.feature.home.navigation.homeGraph
import com.hubenko.feature.status.navigation.StatusRoute
import com.hubenko.feature.status.navigation.statusGraph
import com.hubenko.firestoreapp.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkThemePersistent by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val isDarkTheme = isDarkThemePersistent ?: isSystemInDarkTheme()

            CoreTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination = if (viewModel.isLoggedIn) HomeRoute else AuthRoute

                    NavHost(navController = navController, startDestination = startDestination) {

                        authGraph(
                            onNavigateToHome = {
                                navController.navigate(HomeRoute) {
                                    popUpTo(AuthRoute) { inclusive = true }
                                }
                            }
                        )

                        homeGraph(
                            onNavigateToStatus = { navController.navigate(StatusRoute) },
                            onNavigateToAdmin = { navController.navigate(AdminRoute) },
                            onNavigateToAuth = {
                                navController.navigate(AuthRoute) {
                                    popUpTo(HomeRoute) { inclusive = true }
                                }
                            },
                            onThemeToggle = viewModel::toggleTheme
                        )

                        statusGraph(
                            onNavigateBack = { navController.popBackStack() }
                        )

                        adminGraph(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToRegisterEmployee = { navController.navigate(com.hubenko.feature.admin.navigation.RegisterEmployeeRoute) },
                            onNavigateToReminderSettings = { employeeId ->
                                navController.navigate(com.hubenko.feature.admin.navigation.ReminderSettingsRoute(employeeId))
                            }
                        )
                    }
                }
            }
        }
    }
}
