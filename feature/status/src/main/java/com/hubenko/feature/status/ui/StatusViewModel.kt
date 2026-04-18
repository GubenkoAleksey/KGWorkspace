package com.hubenko.feature.status.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.toUiText
import com.hubenko.domain.repository.AuthDataSource
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.usecase.GetStatusTypesUseCase
import com.hubenko.domain.usecase.SubmitStatusUseCase
import com.hubenko.domain.util.onFailure
import com.hubenko.domain.util.onSuccess
import com.hubenko.feature.status.R
import com.hubenko.feature.status.ui.model.toEmployeeStatusUi
import com.hubenko.feature.status.ui.model.toStatusTypeUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val submitStatusUseCase: SubmitStatusUseCase,
    private val statusRepository: StatusRepository,
    private val authRepository: AuthDataSource,
    private val getStatusTypesUseCase: GetStatusTypesUseCase
) : BaseViewModel<StatusState, StatusIntent, StatusEffect>(StatusState()) {

    init {
        onIntent(StatusIntent.LoadActiveStatus)
        loadStatusTypes()
    }

    private fun loadStatusTypes() {
        viewModelScope.launch {
            getStatusTypesUseCase().collectLatest { types ->
                updateState { copy(statusTypes = types.map { it.toStatusTypeUi() }) }
            }
        }
    }

    override fun onIntent(intent: StatusIntent) {
        when (intent) {
            is StatusIntent.LoadActiveStatus -> loadActiveStatus()
            is StatusIntent.SubmitStatusClick -> {
                updateState { copy(showConfirmDialog = true, pendingStatus = intent.status) }
            }
            is StatusIntent.ConfirmSubmit -> {
                val pending = viewState.value.pendingStatus
                if (pending != null) {
                    val currentNote = viewState.value.note
                    val active = viewState.value.activeStatus
                    updateState { copy(showConfirmDialog = false, pendingStatus = null) }
                    if (active != null && active.status == pending) {
                        finishStatus(active.id)
                    } else {
                        submitStatus(pending, currentNote)
                    }
                }
            }
            is StatusIntent.DismissConfirmDialog -> {
                updateState { copy(showConfirmDialog = false, pendingStatus = null) }
            }
            is StatusIntent.UpdateNote -> {
                if (intent.note.length <= 300) {
                    updateState { copy(note = intent.note) }
                }
            }
            is StatusIntent.OnBackClick -> sendEffect(StatusEffect.NavigateBack)
        }
    }

    private fun loadActiveStatus() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val active = statusRepository.getActiveStatus(userId)
            updateState { copy(activeStatus = active?.toEmployeeStatusUi()) }
        }
    }

    private fun todayAt(hour: Int, minute: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun submitStatus(status: String, note: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val noteToSubmit = note.trim().takeIf { it.isNotEmpty() }
            val startTime = if (status == "Sick") todayAt(8, 0) else null
            val endTime = if (status == "Sick") todayAt(18, 0) else null
            submitStatusUseCase(status, noteToSubmit, startTime, endTime)
                .onSuccess {
                    if (status == "Sick") {
                        updateState { copy(isLoading = false, note = "", activeStatus = null) }
                    } else {
                        loadActiveStatus()
                        updateState { copy(isLoading = false, note = "") }
                    }
                    sendEffect(StatusEffect.ShowSnackbar(UiText.StringResource(R.string.status_updated)))
                }
                .onFailure { error ->
                    val errorUiText = error.toUiText()
                    updateState { copy(isLoading = false, error = errorUiText) }
                    sendEffect(StatusEffect.ShowSnackbar(errorUiText))
                }
        }
    }

    private fun finishStatus(id: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            statusRepository.updateStatusEndTime(id, System.currentTimeMillis())
                .onSuccess {
                    updateState { copy(isLoading = false, activeStatus = null) }
                    sendEffect(StatusEffect.ShowSnackbar(UiText.StringResource(R.string.status_updated)))
                }
                .onFailure { error ->
                    val errorUiText = error.toUiText()
                    updateState { copy(isLoading = false, error = errorUiText) }
                    sendEffect(StatusEffect.ShowSnackbar(errorUiText))
                }
        }
    }
}
