package com.hubenko.feature.admin.ui.employees

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.UiText
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.ui.model.EmployeeUi
import com.hubenko.feature.admin.ui.model.toDomain
import com.hubenko.feature.admin.ui.model.toBaseRateUi
import com.hubenko.feature.admin.ui.model.toEmployeeUi
import com.hubenko.feature.admin.ui.model.toHourlyRateUi
import com.hubenko.feature.admin.ui.model.toReminderSettingsUi
import com.hubenko.feature.admin.ui.model.toRoleUi
import com.hubenko.feature.admin.ui.model.toStatusTypeUi
import com.hubenko.domain.usecase.DeleteEmployeeUseCase
import com.hubenko.domain.usecase.GetAllEmployeesUseCase
import com.hubenko.domain.usecase.GetAllReminderSettingsUseCase
import com.hubenko.domain.usecase.GetBaseRatesUseCase
import com.hubenko.domain.usecase.GetHourlyRatesUseCase
import com.hubenko.domain.usecase.GetRolesUseCase
import com.hubenko.domain.usecase.GetStatusTypesUseCase
import com.hubenko.domain.usecase.SaveEmployeeUseCase
import com.hubenko.domain.util.onFailure
import com.hubenko.domain.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val getAllEmployeesUseCase: GetAllEmployeesUseCase,
    private val saveEmployeeUseCase: SaveEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val getRolesUseCase: GetRolesUseCase,
    private val getBaseRatesUseCase: GetBaseRatesUseCase,
    private val getHourlyRatesUseCase: GetHourlyRatesUseCase,
    private val getAllReminderSettingsUseCase: GetAllReminderSettingsUseCase,
    private val getStatusTypesUseCase: GetStatusTypesUseCase
) : BaseViewModel<EmployeesState, EmployeesIntent, EmployeesEffect>(EmployeesState()) {

    init {
        onIntent(EmployeesIntent.LoadData)
    }

    override fun onIntent(intent: EmployeesIntent) {
        when (intent) {
            is EmployeesIntent.LoadData -> loadData()
            is EmployeesIntent.OnAddEmployeeClick -> sendEffect(EmployeesEffect.NavigateToRegisterEmployee)
            is EmployeesIntent.OnEditEmployeeClick -> updateState {
                copy(isEmployeeDialogOpen = true, editingEmployee = intent.employee)
            }
            is EmployeesIntent.OnDeleteEmployeeClick -> updateState {
                copy(employeePendingDelete = intent.employee)
            }
            is EmployeesIntent.OnConfirmDeleteEmployee -> confirmDeleteEmployee()
            is EmployeesIntent.OnDismissDeleteDialog -> updateState {
                copy(employeePendingDelete = null)
            }
            is EmployeesIntent.OnSaveEmployee -> saveEmployee(intent.employee)
            is EmployeesIntent.OnDismissDialog -> updateState {
                copy(isEmployeeDialogOpen = false, editingEmployee = null)
            }
            is EmployeesIntent.OnReminderClick -> sendEffect(
                EmployeesEffect.NavigateToReminderSettings(intent.employeeId)
            )
            is EmployeesIntent.OnViewStatusesClick -> sendEffect(
                EmployeesEffect.NavigateToEmployeeStatuses(intent.employeeId)
            )
            is EmployeesIntent.OnFilterClick -> updateState { copy(isFilterSheetOpen = true) }
            is EmployeesIntent.OnApplyFilter -> applyFilter(intent.roles, intent.employeeIds)
            is EmployeesIntent.OnClearFilter -> clearFilter()
            is EmployeesIntent.OnDismissFilterSheet -> updateState { copy(isFilterSheetOpen = false) }
        }
    }

    private fun loadData() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            launch {
                combine(
                    getAllEmployeesUseCase(),
                    getAllReminderSettingsUseCase()
                ) { employees, reminderList ->
                    val reminderMap = reminderList.associateBy { it.employeeId }
                    employees.map { employee ->
                        employee.toEmployeeUi().copy(
                            reminderSettings = reminderMap[employee.id]?.toReminderSettingsUi()
                        )
                    }
                }.collectLatest { list ->
                    val s = viewState.value
                    val displayed = applyFilters(list, s.filterRoles, s.filterEmployeeIds)
                    updateState { copy(employees = list, displayedEmployees = displayed, isLoading = false) }
                }
            }
            launch {
                getRolesUseCase().collectLatest { roles ->
                    updateState { copy(roles = roles.map { it.toRoleUi() }) }
                }
            }
            launch {
                getBaseRatesUseCase().collectLatest { rates ->
                    updateState { copy(baseRates = rates.map { it.toBaseRateUi() }) }
                }
            }
            launch {
                getHourlyRatesUseCase().collectLatest { rates ->
                    updateState { copy(hourlyRates = rates.map { it.toHourlyRateUi() }) }
                }
            }
            launch {
                getStatusTypesUseCase().collectLatest { types ->
                    updateState { copy(statusTypes = types.map { it.toStatusTypeUi() }) }
                }
            }
        }
    }

    private fun applyFilter(roles: Set<String>, employeeIds: Set<String>) {
        val displayed = applyFilters(viewState.value.employees, roles, employeeIds)
        updateState {
            copy(filterRoles = roles, filterEmployeeIds = employeeIds, displayedEmployees = displayed, isFilterSheetOpen = false)
        }
    }

    private fun clearFilter() {
        updateState {
            copy(filterRoles = emptySet(), filterEmployeeIds = emptySet(), displayedEmployees = employees, isFilterSheetOpen = false)
        }
    }

    private fun applyFilters(list: List<EmployeeUi>, roles: Set<String>, employeeIds: Set<String>): List<EmployeeUi> =
        list
            .let { if (roles.isEmpty()) it else it.filter { e -> e.role in roles } }
            .let { if (employeeIds.isEmpty()) it else it.filter { e -> e.id in employeeIds } }

    private fun saveEmployee(employeeUi: EmployeeUi) {
        viewModelScope.launch {
            saveEmployeeUseCase(employeeUi.toDomain())
                .onSuccess {
                    updateState { copy(isEmployeeDialogOpen = false, editingEmployee = null) }
                    sendEffect(EmployeesEffect.ShowSnackbar(UiText.StringResource(R.string.success_employee_saved)))
                }
                .onFailure {
                    sendEffect(EmployeesEffect.ShowSnackbar(UiText.StringResource(R.string.error_save_failed)))
                }
        }
    }

    private fun confirmDeleteEmployee() {
        val employeeId = viewState.value.employeePendingDelete?.id ?: return
        viewModelScope.launch {
            deleteEmployeeUseCase(employeeId)
                .onSuccess {
                    updateState { copy(employeePendingDelete = null) }
                    sendEffect(EmployeesEffect.ShowSnackbar(UiText.StringResource(R.string.success_employee_deleted)))
                }
                .onFailure {
                    sendEffect(EmployeesEffect.ShowSnackbar(UiText.StringResource(R.string.error_delete_failed)))
                }
        }
    }
}
