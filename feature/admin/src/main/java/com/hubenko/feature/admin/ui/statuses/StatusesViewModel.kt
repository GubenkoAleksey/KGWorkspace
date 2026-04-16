package com.hubenko.feature.admin.ui.statuses

import android.app.Application
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.toUiText
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.ui.model.EmployeeStatusUi
import com.hubenko.feature.admin.ui.model.toEmployeeStatusUi
import com.hubenko.feature.admin.ui.model.StatusTypeUi
import com.hubenko.feature.admin.ui.model.toStatusTypeUi
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.usecase.GetAllStatusesUseCase
import com.hubenko.domain.usecase.GetStatusTypesUseCase
import com.hubenko.domain.util.onFailure
import com.hubenko.domain.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

@HiltViewModel
class StatusesViewModel @Inject constructor(
    private val application: Application,
    private val getAllStatusesUseCase: GetAllStatusesUseCase,
    private val statusRepository: StatusRepository,
    private val getStatusTypesUseCase: GetStatusTypesUseCase
) : BaseViewModel<StatusesState, StatusesIntent, StatusesEffect>(StatusesState()) {

    init {
        onIntent(StatusesIntent.LoadData)
        loadStatusTypes()
    }

    private fun loadStatusTypes() {
        viewModelScope.launch {
            getStatusTypesUseCase().collectLatest { types ->
                updateState { copy(availableStatusTypes = types.map { it.toStatusTypeUi() }) }
            }
        }
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
            is StatusesIntent.OnApplyFilter -> applyFilter(intent.from, intent.to, intent.employeeIds, intent.statusTypes)
            is StatusesIntent.OnClearFilter -> clearFilter()
        }
    }

    private fun loadData() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            getAllStatusesUseCase().collectLatest { list ->
                val sortedStatuses = list.map { it.toEmployeeStatusUi() }.sortedByDescending { it.startTime }
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
        allStatuses: List<EmployeeStatusUi>,
        from: Long?,
        to: Long?,
        employeeIds: Set<String> = viewState.value.filterEmployeeIds,
        statusTypes: Set<String> = viewState.value.filterStatusTypes
    ): List<EmployeeStatusesGroup> {
        val filtered = allStatuses
            .let { list -> if (from != null && to != null) list.filter { it.startTime in from..to } else list }
            .let { list -> if (employeeIds.isNotEmpty()) list.filter { it.employeeId in employeeIds } else list }
            .let { list -> if (statusTypes.isNotEmpty()) list.filter { it.status in statusTypes } else list }
        val expandedByEmployeeId = viewState.value.employeeGroups.associate { it.employeeId to it.isExpanded }
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

    private fun applyFilter(from: Long?, to: Long?, employeeIds: Set<String>, statusTypes: Set<String>) {
        val groups = buildGroups(viewState.value.statuses, from, to, employeeIds, statusTypes)
        updateState {
            copy(
                filterDateFrom = from,
                filterDateTo = to,
                filterEmployeeIds = employeeIds,
                filterStatusTypes = statusTypes,
                employeeGroups = groups,
                isFilterSheetOpen = false
            )
        }
    }

    private fun clearFilter() {
        val groups = buildGroups(viewState.value.statuses, null, null, emptySet(), emptySet())
        updateState {
            copy(
                filterDateFrom = null,
                filterDateTo = null,
                filterEmployeeIds = emptySet(),
                filterStatusTypes = emptySet(),
                employeeGroups = groups,
                isFilterSheetOpen = false
            )
        }
    }

    private fun toggleEmployeeGroup(employeeId: String) {
        updateState {
            copy(
                employeeGroups = employeeGroups.map { group ->
                    if (group.employeeId == employeeId) group.copy(isExpanded = !group.isExpanded)
                    else group
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
                    FileProvider.getUriForFile(application, "com.hubenko.firestoreapp.fileprovider", file)
                }
                sendEffect(StatusesEffect.ShareFile(uri))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                sendEffect(StatusesEffect.ShowSnackbar(UiText.StringResource(R.string.error_export_failed)))
            }
        }
    }

    private fun generateCsvContent(statuses: List<EmployeeStatusUi>): String {
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
        val file = File(application.cacheDir, "status_$dateString.csv")
        file.writeText(content)
        return file
    }

    private fun deleteAllStatuses() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isDeleteDialogOpen = false) }
            statusRepository.deleteAllStatuses()
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    sendEffect(StatusesEffect.ShowSnackbar(UiText.StringResource(R.string.success_statuses_deleted)))
                }
                .onFailure { error ->
                    updateState { copy(isLoading = false) }
                    sendEffect(StatusesEffect.ShowSnackbar(error.toUiText()))
                }
        }
    }
}
