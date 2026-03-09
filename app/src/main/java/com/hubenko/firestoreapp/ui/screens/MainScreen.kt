package com.hubenko.firestoreapp.ui.screens

import androidx.compose.runtime.*
import com.hubenko.firestoreapp.ui.viewmodel.StatusViewModel

@Composable
fun MainScreen(viewModel: StatusViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    MainContent(
        uiState = uiState,
        onStatusSubmit = { status -> viewModel.submitStatus(status) },
        onDismissDialog = { viewModel.resetState() }
    )
}
