package com.hubenko.feature.admin.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hubenko.feature.admin.ui.AdminScreen
import com.hubenko.feature.admin.ui.register.RegisterEmployeeScreen
import com.hubenko.feature.admin.ui.reminder.ReminderSettingsScreen

fun NavGraphBuilder.adminGraph(
    onNavigateBack: () -> Unit,
    onNavigateToRegisterEmployee: () -> Unit,
    onNavigateToReminderSettings: (String) -> Unit
) {
    composable<AdminRoute> {
        AdminScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToReminderSettings = onNavigateToReminderSettings,
            onNavigateToRegisterEmployee = onNavigateToRegisterEmployee
        )
    }
    composable<RegisterEmployeeRoute> {
        RegisterEmployeeScreen(onNavigateBack = onNavigateBack)
    }
    composable<ReminderSettingsRoute> {
        ReminderSettingsScreen(onBack = onNavigateBack)
    }
}
