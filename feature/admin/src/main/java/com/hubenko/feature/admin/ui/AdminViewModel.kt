package com.hubenko.feature.admin.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.model.Employee
import com.hubenko.domain.usecase.DeleteEmployeeUseCase
import com.hubenko.domain.usecase.GetAllEmployeesUseCase
import com.hubenko.domain.usecase.GetAllStatusesUseCase
import com.hubenko.domain.usecase.SaveEmployeeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel для екрана адміністратора.
 * Керує бізнес-логікою отримання даних співробітників та їхніх статусів,
 * а також операціями створення, редагування та видалення.
 *
 * @property getAllEmployeesUseCase Отримання списку співробітників.
 * @property getAllStatusesUseCase Отримання списку статусів.
 * @property saveEmployeeUseCase Збереження даних співробітника.
 * @property deleteEmployeeUseCase Видалення співробітника.
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAllEmployeesUseCase: GetAllEmployeesUseCase,
    private val getAllStatusesUseCase: GetAllStatusesUseCase,
    private val saveEmployeeUseCase: SaveEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase
) : BaseViewModel<AdminState, AdminIntent, AdminEffect>(AdminState()) {

    init {
        onIntent(AdminIntent.LoadData)
    }

    override fun onIntent(intent: AdminIntent) {
        when (intent) {
            is AdminIntent.LoadData -> loadData()
            is AdminIntent.OnTabSelected -> updateState { copy(selectedTab = intent.tab) }
            is AdminIntent.OnAddEmployeeClick -> updateState { 
                copy(isEmployeeDialogOpen = true, editingEmployee = null) 
            }
            is AdminIntent.OnEditEmployeeClick -> updateState { 
                copy(isEmployeeDialogOpen = true, editingEmployee = intent.employee) 
            }
            is AdminIntent.OnDeleteEmployeeClick -> deleteEmployee(intent.id)
            is AdminIntent.OnSaveEmployee -> saveEmployee(intent.employee)
            is AdminIntent.OnDismissDialog -> updateState { 
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
                getAllStatusesUseCase().collectLatest { list ->
                    updateState { copy(statuses = list) }
                }
            }
        }
    }

    private fun saveEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                val employeeToSave = if (employee.id.isBlank()) {
                    employee.copy(id = UUID.randomUUID().toString())
                } else {
                    employee
                }
                saveEmployeeUseCase(employeeToSave)
                updateState { copy(isEmployeeDialogOpen = false, editingEmployee = null) }
                sendEffect(AdminEffect.ShowToast("Співробітника збережено"))
            } catch (e: Exception) {
                sendEffect(AdminEffect.ShowToast("Помилка збереження: \${e.message}"))
            }
        }
    }

    private fun deleteEmployee(id: String) {
        viewModelScope.launch {
            try {
                deleteEmployeeUseCase(id)
                sendEffect(AdminEffect.ShowToast("Співробітника видалено"))
            } catch (e: Exception) {
                sendEffect(AdminEffect.ShowToast("Помилка видалення: \${e.message}"))
            }
        }
    }
}
