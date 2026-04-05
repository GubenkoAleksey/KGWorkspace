package com.hubenko.feature.admin.ui.reminder

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun ReminderSettingsScreen(
    employeeId: String,
    viewModel: ReminderSettingsViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(employeeId) {
        viewModel.onIntent(ReminderSettingsIntent.LoadSettings(employeeId))
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            snackbarHostState.showSnackbar("Налаштування успішно збережено")
            onBack()
        }
    }

    ReminderSettingsContent(
        state = state,
        onIntent = viewModel::onIntent,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    )
}
