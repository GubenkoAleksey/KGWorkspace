package com.hubenko.feature.admin.ui

import android.app.Application
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.model.Employee
import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.usecase.DeleteEmployeeUseCase
import com.hubenko.domain.usecase.GetAllEmployeesUseCase
import com.hubenko.domain.usecase.GetAllStatusesUseCase
import com.hubenko.domain.usecase.SaveEmployeeUseCase
import com.hubenko.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel для екрана адміністратора.
 * Керує бізнес-логікою отримання даних співробітників та їхніх статусів,
 * а також операціями створення, редагування та видалення.
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val application: Application,
    private val getAllEmployeesUseCase: GetAllEmployeesUseCase,
    private val getAllStatusesUseCase: GetAllStatusesUseCase,
    private val saveEmployeeUseCase: SaveEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val statusRepository: StatusRepository
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
                copy(isEmployeeDialogOpen = false, editingEmployee = null, isDeleteStatusesDialogOpen = false) 
            }
            is AdminIntent.OnExportStatusesClick -> exportStatusesToCsv()
            is AdminIntent.OnDeleteAllStatusesClick -> updateState { copy(isDeleteStatusesDialogOpen = true) }
            is AdminIntent.OnConfirmDeleteAllStatuses -> deleteAllStatuses()
            is AdminIntent.OnEmployeeSelectedForSchedule -> sendEffect(AdminEffect.NavigateToReminderSettings(intent.employeeId))
            is AdminIntent.OnBackClick -> handleBackClick()
        }
    }

    private fun handleBackClick() {
        if (viewState.value.selectedTab == AdminTab.DASHBOARD) {
            sendEffect(AdminEffect.NavigateBack)
        } else {
            updateState { copy(selectedTab = AdminTab.DASHBOARD) }
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

    private fun exportStatusesToCsv() {
        val statuses = viewState.value.statuses
        if (statuses.isEmpty()) return

        viewModelScope.launch {
            try {
                val uri = withContext(Dispatchers.IO) {
                    val csvString = generateCsvContent(statuses)
                    val file = saveCsvToFile(csvString)
                    FileProvider.getUriForFile(
                        application,
                        "com.hubenko.firestoreapp.fileprovider",
                        file
                    )
                }
                sendEffect(AdminEffect.ShareFile(uri))
            } catch (e: Exception) {
                sendEffect(AdminEffect.ShowToast("Помилка експорту: ${e.message}"))
            }
        }
    }

    private fun generateCsvContent(statuses: List<EmployeeStatus>): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        // Header
        sb.append("ID;ПІБ;Статус;Початок;Кінець\n")
        // Rows
        statuses.forEach { status ->
            val start = sdf.format(Date(status.startTime))
            val end = status.endTime?.let { sdf.format(Date(it)) } ?: "-"
            val fullName = status.employeeFullName ?: "Невідомо"
            sb.append("${status.employeeId};${fullName};${status.status};${start};${end}\n")
        }
        return sb.toString()
    }

    private fun saveCsvToFile(content: String): File {
        val dateString = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())
        val fileName = "status_$dateString.csv"
        val file = File(application.cacheDir, fileName)
        file.writeText(content)
        return file
    }

    private fun saveEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                if (employee.id.isBlank()) {
                    // Новий користувач - створюємо через FirebaseAuth
                    signUpUseCase(employee).onSuccess {
                        updateState { copy(isEmployeeDialogOpen = false, editingEmployee = null) }
                        sendEffect(AdminEffect.ShowToast("Співробітника створено в системі"))
                    }.onFailure {
                        sendEffect(AdminEffect.ShowToast("Помилка створення Auth: ${it.message}"))
                    }
                } else {
                    // Існуючий користувач - просто оновлюємо Firestore та локальну БД
                    saveEmployeeUseCase(employee)
                    updateState { copy(isEmployeeDialogOpen = false, editingEmployee = null) }
                    sendEffect(AdminEffect.ShowToast("Дані співробітника оновлено"))
                }
            } catch (e: Exception) {
                sendEffect(AdminEffect.ShowToast("Помилка збереження: ${e.message}"))
            }
        }
    }

    private fun deleteEmployee(id: String) {
        viewModelScope.launch {
            try {
                deleteEmployeeUseCase(id)
                sendEffect(AdminEffect.ShowToast("Співробітника видалено"))
            } catch (e: Exception) {
                sendEffect(AdminEffect.ShowToast("Помилка видалення: ${e.message}"))
            }
        }
    }

    private fun deleteAllStatuses() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isDeleteStatusesDialogOpen = false) }
            statusRepository.deleteAllStatuses()
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    sendEffect(AdminEffect.ShowToast("Усі статуси успішно видалено"))
                }
                .onFailure { e ->
                    updateState { copy(isLoading = false) }
                    sendEffect(AdminEffect.ShowToast("Помилка видалення: ${e.message}"))
                }
        }
    }
}
