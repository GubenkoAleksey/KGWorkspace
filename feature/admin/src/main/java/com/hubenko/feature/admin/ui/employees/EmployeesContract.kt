package com.hubenko.feature.admin.ui.employees

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.Employee
import com.hubenko.domain.model.Role

data class EmployeesState(
    val employees: List<Employee> = emptyList(),
    val roles: List<Role> = emptyList(),
    val isLoading: Boolean = false,
    val isEmployeeDialogOpen: Boolean = false,
    val editingEmployee: Employee? = null,
    val employeePendingDelete: Employee? = null
) : ViewState

sealed class EmployeesIntent : ViewIntent {
    data object LoadData : EmployeesIntent()
    data object OnAddEmployeeClick : EmployeesIntent()
    data class OnEditEmployeeClick(val employee: Employee) : EmployeesIntent()
    data class OnDeleteEmployeeClick(val employee: Employee) : EmployeesIntent()
    data object OnConfirmDeleteEmployee : EmployeesIntent()
    data object OnDismissDeleteDialog : EmployeesIntent()
    data class OnSaveEmployee(val employee: Employee) : EmployeesIntent()
    data object OnDismissDialog : EmployeesIntent()
}

sealed class EmployeesEffect : ViewSideEffect {
    data object NavigateToRegisterEmployee : EmployeesEffect()
    data class ShowToast(val message: String) : EmployeesEffect()
}

