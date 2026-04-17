package com.hubenko.feature.admin.ui.employees

import androidx.compose.runtime.Stable
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState
import com.hubenko.feature.admin.ui.model.BaseRateUi
import com.hubenko.feature.admin.ui.model.EmployeeUi
import com.hubenko.feature.admin.ui.model.HourlyRateUi
import com.hubenko.feature.admin.ui.model.RoleUi

@Stable
data class EmployeesState(
    val employees: List<EmployeeUi> = emptyList(),
    val roles: List<RoleUi> = emptyList(),
    val baseRates: List<BaseRateUi> = emptyList(),
    val hourlyRates: List<HourlyRateUi> = emptyList(),
    val isLoading: Boolean = false,
    val isEmployeeDialogOpen: Boolean = false,
    val editingEmployee: EmployeeUi? = null,
    val employeePendingDelete: EmployeeUi? = null
) : ViewState

sealed interface EmployeesIntent : ViewIntent {
    data object LoadData : EmployeesIntent
    data object OnAddEmployeeClick : EmployeesIntent
    data class OnEditEmployeeClick(val employee: EmployeeUi) : EmployeesIntent
    data class OnDeleteEmployeeClick(val employee: EmployeeUi) : EmployeesIntent
    data object OnConfirmDeleteEmployee : EmployeesIntent
    data object OnDismissDeleteDialog : EmployeesIntent
    data class OnSaveEmployee(val employee: EmployeeUi) : EmployeesIntent
    data object OnDismissDialog : EmployeesIntent
    data class OnReminderClick(val employeeId: String) : EmployeesIntent
    data class OnViewStatusesClick(val employeeId: String) : EmployeesIntent
}

sealed interface EmployeesEffect : ViewSideEffect {
    data object NavigateToRegisterEmployee : EmployeesEffect
    data class NavigateToReminderSettings(val employeeId: String) : EmployeesEffect
    data class NavigateToEmployeeStatuses(val employeeId: String) : EmployeesEffect
    data class ShowSnackbar(val message: UiText) : EmployeesEffect
}
