package com.hubenko.firestoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        // Corrected: Added database.employeeDao() as required by AuthRepository
        val authRepository = AuthRepository(firebaseAuth, firestore, database.employeeDao())
        
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
                    val userRole by authViewModel.userRole.collectAsState()
                    val currentUser = authViewModel.currentUser

                    // If user is already logged in, fetch their role
                    LaunchedEffect(currentUser) {
                        if (currentUser != null && userRole == null) {
                            authViewModel.fetchUserRole(currentUser.uid)
                        }
                    }

                    val startDestination = if (currentUser != null) "home_redirect" else "login"
                    
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = { isAdmin ->
                                    navController.navigate("home/$isAdmin") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        // Auxiliary route to handle auto-login redirection
                        composable("home_redirect") {
                            LaunchedEffect(userRole) {
                                if (userRole != null) {
                                    val isAdmin = userRole == "admin" || userRole == "ADMIN"
                                    navController.navigate("home/$isAdmin") {
                                        popUpTo("home_redirect") { inclusive = true }
                                    }
                                }
                            }
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
