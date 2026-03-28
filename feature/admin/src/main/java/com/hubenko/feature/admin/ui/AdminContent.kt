package com.hubenko.feature.admin.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.feature.admin.ui.dashboard.DashboardContent
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
    onTabSelected: (AdminTab) -> Unit,
    onBackToMenu: () -> Unit,
    onNavigateToReminderSettings: (String) -> Unit,
    onNavigateToRegisterEmployee: () -> Unit
) {
    when (state.selectedTab) {
        AdminTab.DASHBOARD -> DashboardContent(
            onTabSelected = onTabSelected
        )
        AdminTab.EMPLOYEES -> EmployeesScreen(
            onNavigateToRegister = onNavigateToRegisterEmployee,
            onBackClick = onBackToMenu
        )
        AdminTab.STATUSES -> StatusesScreen(
            onBackClick = onBackToMenu
        )
        AdminTab.SCHEDULE -> ScheduleScreen(
            onNavigateToReminderSettings = onNavigateToReminderSettings,
            onBackClick = onBackToMenu
        )
    }
}

@Preview(showBackground = true, name = "Dashboard")
@Composable
private fun AdminContentDashboardPreview() {
    CoreTheme {
        AdminContent(
            state = AdminState(selectedTab = AdminTab.DASHBOARD),
            onTabSelected = {},
            onBackToMenu = {},
            onNavigateToReminderSettings = {},
            onNavigateToRegisterEmployee = {}
        )
    }
}
