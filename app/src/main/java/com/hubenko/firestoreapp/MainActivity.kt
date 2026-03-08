package com.hubenko.firestoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.firestoreapp.data.local.AppDatabase
import com.hubenko.firestoreapp.data.repository.StatusRepository
import com.hubenko.firestoreapp.ui.screens.MainScreen
import com.hubenko.firestoreapp.ui.theme.FirestoreAppTheme
import com.hubenko.firestoreapp.ui.viewmodel.StatusViewModel
import com.hubenko.firestoreapp.ui.viewmodel.StatusViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Manual dependency injection for demo
        val database = AppDatabase.getDatabase(this)
        val firestore = FirebaseFirestore.getInstance()
        val repository = StatusRepository(this, database.employeeStatusDao(), firestore)
        
        val viewModel: StatusViewModel by viewModels {
            StatusViewModelFactory(repository)
        }

        setContent {
            FirestoreAppTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
