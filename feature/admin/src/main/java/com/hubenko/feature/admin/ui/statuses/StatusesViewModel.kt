package com.hubenko.feature.admin.ui.statuses

import android.app.Application
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.usecase.GetAllStatusesUseCase
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
 * ViewModel для екрана статусів співробітників.
 * Відповідає за завантаження, експорт CSV та видалення всіх статусів.
 */
@HiltViewModel
class StatusesViewModel @Inject constructor(
    private val application: Application,
    private val getAllStatusesUseCase: GetAllStatusesUseCase,
    private val statusRepository: StatusRepository
) : BaseViewModel<StatusesState, StatusesIntent, StatusesEffect>(StatusesState()) {

    init {
        onIntent(StatusesIntent.LoadData)
    }

    override fun onIntent(intent: StatusesIntent) {
        when (intent) {
            is StatusesIntent.LoadData -> loadData()
            is StatusesIntent.OnExportClick -> exportStatusesToCsv()
            is StatusesIntent.OnDeleteAllClick -> updateState { copy(isDeleteDialogOpen = true) }
            is StatusesIntent.OnConfirmDelete -> deleteAllStatuses()
            is StatusesIntent.OnDismissDialog -> updateState { copy(isDeleteDialogOpen = false) }
            is StatusesIntent.OnEmployeeExpandToggle -> toggleEmployeeGroup(intent.employeeId)
            is StatusesIntent.OnFilterClick -> updateState { copy(isFilterSheetOpen = true) }
            is StatusesIntent.OnDismissFilterSheet -> updateState { copy(isFilterSheetOpen = false) }
            is StatusesIntent.OnApplyFilter -> applyFilter(intent.from, intent.to)
            is StatusesIntent.OnClearFilter -> clearFilter()
        }
    }

    private fun loadData() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            getAllStatusesUseCase().collectLatest { list ->
                val sortedStatuses = list.sortedByDescending { it.startTime }
                val currentState = viewState.value
                updateState {
                    copy(
                        statuses = sortedStatuses,
                        employeeGroups = buildGroups(sortedStatuses, filterDateFrom, filterDateTo),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun buildGroups(
        allStatuses: List<EmployeeStatus>,
        from: Long?,
        to: Long?
    ): List<EmployeeStatusesGroup> {
        val filtered = if (from != null && to != null) {
            allStatuses.filter { it.startTime in from..to }
        } else {
            allStatuses
        }
        val expandedByEmployeeId = viewState.value.employeeGroups.associate {
            it.employeeId to it.isExpanded
        }
        return filtered
            .groupBy { it.employeeId }
            .map { (employeeId, statuses) ->
                val employeeName = statuses
                    .firstNotNullOfOrNull { it.employeeFullName?.takeIf(String::isNotBlank) }
                    ?: "ID Працівника: $employeeId"
                EmployeeStatusesGroup(
                    employeeId = employeeId,
                    employeeName = employeeName,
                    statuses = statuses.sortedByDescending { it.startTime },
                    isExpanded = expandedByEmployeeId[employeeId] ?: false
                )
            }
            .sortedByDescending { it.statuses.firstOrNull()?.startTime ?: Long.MIN_VALUE }
    }

    private fun applyFilter(from: Long?, to: Long?) {
        val groups = buildGroups(viewState.value.statuses, from, to)
        updateState { copy(filterDateFrom = from, filterDateTo = to, employeeGroups = groups, isFilterSheetOpen = false) }
    }

    private fun clearFilter() {
        val groups = buildGroups(viewState.value.statuses, null, null)
        updateState { copy(filterDateFrom = null, filterDateTo = null, employeeGroups = groups, isFilterSheetOpen = false) }
    }

    private fun toggleEmployeeGroup(employeeId: String) {
        updateState {
            copy(
                employeeGroups = employeeGroups.map { group ->
                    if (group.employeeId == employeeId) {
                        group.copy(isExpanded = !group.isExpanded)
                    } else {
                        group
                    }
                }
            )
        }
    }

    private fun exportStatusesToCsv() {
        val statuses = viewState.value.employeeGroups.flatMap { it.statuses }
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
                sendEffect(StatusesEffect.ShareFile(uri))
            } catch (e: Exception) {
                sendEffect(StatusesEffect.ShowToast("Помилка експорту: ${e.message}"))
            }
        }
    }

    private fun generateCsvContent(statuses: List<EmployeeStatus>): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("ID;ПІБ;Статус;Початок;Кінець\n")
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

    private fun deleteAllStatuses() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isDeleteDialogOpen = false) }
            statusRepository.deleteAllStatuses()
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    sendEffect(StatusesEffect.ShowToast("Усі статуси успішно видалено"))
                }
                .onFailure { e ->
                    updateState { copy(isLoading = false) }
                    sendEffect(StatusesEffect.ShowToast("Помилка видалення: ${e.message}"))
                }
        }
    }
}

