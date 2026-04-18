package com.hubenko.feature.admin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hubenko.feature.admin.ui.AdminScreen
import com.hubenko.feature.admin.ui.register.RegisterEmployeeScreen
import com.hubenko.feature.admin.ui.reminder.ReminderSettingsScreen
import com.hubenko.feature.admin.ui.statuses.StatusesScreen

fun NavGraphBuilder.adminGraph(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onNavigateToRegisterEmployee: () -> Unit,
    onNavigateToReminderSettings: (String) -> Unit
) {
    composable<AdminRoute> {
        AdminScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToReminderSettings = onNavigateToReminderSettings,
            onNavigateToRegisterEmployee = onNavigateToRegisterEmployee,
            onNavigateToEmployeeStatuses = { employeeId ->
                navController.navigate(EmployeeStatusesRoute(employeeId))
            }
        )
    }
    composable<RegisterEmployeeRoute> {
        RegisterEmployeeScreen(onNavigateBack = onNavigateBack)
    }
    composable<ReminderSettingsRoute> {
        ReminderSettingsScreen(onBack = onNavigateBack)
    }
    composable<EmployeeStatusesRoute> {
        StatusesScreen(onNavigateBack = onNavigateBack)
    }
}
