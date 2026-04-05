package com.hubenko.feature.admin.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.feature.admin.ui.dashboard.DashboardContent
import com.hubenko.feature.admin.ui.directories.DirectoriesScreen
import com.hubenko.feature.admin.ui.employees.EmployeesScreen
import com.hubenko.feature.admin.ui.schedule.ScheduleScreen
import com.hubenko.feature.admin.ui.statuses.StatusesScreen

/**
 * Stateless router-composable панелі адміністратора.
 * Делегує рендеринг відповідному sub-screen залежно від [AdminState.selectedTab].
 */
@Composable
fun AdminContent(
    state: AdminState,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onTabSelected: (AdminTab) -> Unit,
    onNavigateToReminderSettings: (String) -> Unit,
    onNavigateToRegisterEmployee: () -> Unit
) {
    when (state.selectedTab) {
        AdminTab.DASHBOARD -> DashboardContent(
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle,
            onTabSelected = onTabSelected
        )
        AdminTab.EMPLOYEES -> EmployeesScreen(
            onNavigateToRegister = onNavigateToRegisterEmployee,
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle
        )
        AdminTab.STATUSES -> StatusesScreen(
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle
        )
        AdminTab.SCHEDULE -> ScheduleScreen(
            onNavigateToReminderSettings = onNavigateToReminderSettings,
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle
        )
        AdminTab.DIRECTORIES -> DirectoriesScreen(
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle
        )
    }
}

@Preview(showBackground = true, name = "Dashboard")
@Composable
private fun AdminContentDashboardPreview() {
    CoreTheme {
        AdminContent(
            state = AdminState(selectedTab = AdminTab.DASHBOARD),
            isDarkTheme = false,
            onThemeToggle = {},
            onTabSelected = {},
            onNavigateToReminderSettings = {},
            onNavigateToRegisterEmployee = {}
        )
    }
}
