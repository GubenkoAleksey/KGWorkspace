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
        }
    }

    private fun loadData() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            getAllStatusesUseCase().collectLatest { list ->
                updateState { copy(statuses = list, isLoading = false) }
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

