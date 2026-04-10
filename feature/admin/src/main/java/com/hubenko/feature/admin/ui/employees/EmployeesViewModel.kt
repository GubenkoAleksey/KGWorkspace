package com.hubenko.feature.admin.ui.employees

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.UiText
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.ui.model.EmployeeUi
import com.hubenko.feature.admin.ui.model.toDomain
import com.hubenko.feature.admin.ui.model.toEmployeeUi
import com.hubenko.feature.admin.ui.model.toRoleUi
import com.hubenko.domain.usecase.DeleteEmployeeUseCase
import com.hubenko.domain.usecase.GetAllEmployeesUseCase
import com.hubenko.domain.usecase.GetRolesUseCase
import com.hubenko.domain.usecase.SaveEmployeeUseCase
import com.hubenko.domain.util.onFailure
import com.hubenko.domain.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val getAllEmployeesUseCase: GetAllEmployeesUseCase,
    private val saveEmployeeUseCase: SaveEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val getRolesUseCase: GetRolesUseCase
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
        }
    }

    private fun loadData() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            launch {
                getAllEmployeesUseCase().collectLatest { list ->
                    updateState { copy(employees = list.map { it.toEmployeeUi() }, isLoading = false) }
                }
            }
            launch {
                getRolesUseCase().collectLatest { roles ->
                    updateState { copy(roles = roles.map { it.toRoleUi() }) }
                }
            }
        }
    }

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
