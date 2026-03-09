package com.hubenko.firestoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hubenko.feature.home.ui.HomeScreen
import com.hubenko.feature.status.ui.StatusScreen
import com.hubenko.firestoreapp.ui.auth.LoginScreen
import com.hubenko.firestoreapp.ui.theme.FirestoreAppTheme
import com.hubenko.firestoreapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FirestoreAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    
                    val userRole by authViewModel.userRole.collectAsState()
                    val currentUserId = authViewModel.currentUserId

                    LaunchedEffect(currentUserId) {
                        if (currentUserId != null && userRole == null) {
                            authViewModel.fetchUserRole(currentUserId)
                        }
                    }

                    val startDestination = if (currentUserId != null) "home" else "login"

                    NavHost(navController = navController, startDestination = startDestination) {
                        
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = { _ ->
                                    // The HomeViewModel internally checks admin status via UseCase
                                    // so we don't need to pass isAdmin as navigation arg anymore.
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onNavigateToStatus = { navController.navigate("status") }
                            )
                        }

                        composable("status") {
                            StatusScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
