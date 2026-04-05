package com.hubenko.feature.admin.ui.employees

import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.model.Employee
import com.hubenko.domain.usecase.DeleteEmployeeUseCase
import com.hubenko.domain.usecase.GetAllEmployeesUseCase
import com.hubenko.domain.usecase.GetRolesUseCase
import com.hubenko.domain.usecase.SaveEmployeeUseCase
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
                    updateState { copy(employees = list, isLoading = false) }
                }
            }
            launch {
                getRolesUseCase().collectLatest { roles ->
                    updateState { copy(roles = roles) }
                }
            }
        }
    }

    private fun saveEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                saveEmployeeUseCase(employee)
                updateState { copy(isEmployeeDialogOpen = false, editingEmployee = null) }
                sendEffect(EmployeesEffect.ShowToast("Дані співробітника оновлено"))
            } catch (e: Exception) {
                sendEffect(EmployeesEffect.ShowToast("Помилка збереження: ${e.message}"))
            }
        }
    }

    private fun confirmDeleteEmployee() {
        val employeeId = viewState.value.employeePendingDelete?.id ?: return
        viewModelScope.launch {
            try {
                deleteEmployeeUseCase(employeeId)
                updateState { copy(employeePendingDelete = null) }
                sendEffect(EmployeesEffect.ShowToast("Співробітника видалено"))
            } catch (e: Exception) {
                sendEffect(EmployeesEffect.ShowToast("Помилка видалення: ${e.message}"))
            }
        }
    }
}



