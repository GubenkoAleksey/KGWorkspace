package com.hubenko.firestoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.firestoreapp.data.local.AppDatabase
import com.hubenko.firestoreapp.data.repository.AuthRepository
import com.hubenko.firestoreapp.data.repository.StatusRepository
import com.hubenko.firestoreapp.ui.auth.LoginScreen
import com.hubenko.firestoreapp.ui.auth.RoleSelectionScreen
import com.hubenko.firestoreapp.ui.home.HomeMenuScreen
import com.hubenko.firestoreapp.ui.screens.MainScreen
import com.hubenko.firestoreapp.ui.theme.FirestoreAppTheme
import com.hubenko.firestoreapp.ui.viewmodel.AuthViewModel
import com.hubenko.firestoreapp.ui.viewmodel.AuthViewModelFactory
import com.hubenko.firestoreapp.ui.viewmodel.StatusViewModel
import com.hubenko.firestoreapp.ui.viewmodel.StatusViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Dependency injection
        val database = AppDatabase.getDatabase(this)
        val firestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        
        val statusRepository = StatusRepository(this, database.employeeStatusDao(), firestore)
        val authRepository = AuthRepository(firebaseAuth)
        
        val statusViewModel: StatusViewModel by viewModels {
            StatusViewModelFactory(statusRepository)
        }
        
        val authViewModel: AuthViewModel by viewModels {
            AuthViewModelFactory(authRepository)
        }

        setContent {
            FirestoreAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Start from login if not authenticated
                    val startDestination = if (authRepository.isUserLoggedIn()) "role_selection" else "login"
                    
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.navigate("role_selection") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("role_selection") {
                            RoleSelectionScreen(
                                onRoleSelected = { isAdmin ->
                                    navController.navigate("home/$isAdmin")
                                }
                            )
                        }
                        composable(
                            route = "home/{isAdmin}",
                            arguments = listOf(navArgument("isAdmin") { type = NavType.BoolType })
                        ) { backStackEntry ->
                            val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false
                            HomeMenuScreen(
                                onNavigateToStatus = { navController.navigate("status") },
                                isAdmin = isAdmin
                            )
                        }
                        composable("status") {
                            MainScreen(viewModel = statusViewModel)
                        }
                    }
                }
            }
        }
    }
}
