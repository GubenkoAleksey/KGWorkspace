package com.hubenko.feature.admin.ui.reminder

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReminderSettingsScreen(
    employeeId: String,
    viewModel: ReminderSettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(employeeId) {
        viewModel.onIntent(ReminderSettingsIntent.LoadSettings(employeeId))
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            Toast.makeText(context, "Налаштування успішно збережено", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    ReminderSettingsContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack
    )
}
