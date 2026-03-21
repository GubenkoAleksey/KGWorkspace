package com.hubenko.feature.status.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.repository.AuthRepository
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.usecase.SubmitStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val submitStatusUseCase: SubmitStatusUseCase,
    private val statusRepository: StatusRepository,
    private val authRepository: AuthRepository
) : BaseViewModel<StatusState, StatusIntent, StatusEffect>(StatusState()) {

    init {
        onIntent(StatusIntent.LoadActiveStatus)
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
            is StatusIntent.DismissDialog -> dismissDialog()
            is StatusIntent.OnBackClick -> sendEffect(StatusEffect.NavigateBack)
        }
    }

    private fun loadActiveStatus() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val active = statusRepository.getActiveStatus(userId)
            updateState { copy(activeStatus = active) }
        }
    }

    private fun submitStatus(status: String, note: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            
            val noteToSubmit = note.trim().takeIf { it.isNotEmpty() }
            
            submitStatusUseCase(status, noteToSubmit)
                .onSuccess {
                    if (status == "Sick") {
                        // Для лікарняного робота відразу вважається закритою за вашою логікою? 
                        // Чи лікарняний теж активний? 
                        // Згідно ТЗ: "якщо вибрав Лікарняний то проставляється startTime. І екран оновлюється з вибором статусів."
                        // Це означає що він НЕ активний (завершений відразу або просто не блокує).
                        // Але зазвичай лікарняний триває. 
                        // Проте я зроблю як у ТЗ: "оновлюється з вибором статусів" = activeStatus = null.
                        updateState { copy(isLoading = false, isSuccess = true, note = "", activeStatus = null) }
                    } else {
                        // Офіс або Віддалено - стають активними
                        loadActiveStatus()
                        updateState { copy(isLoading = false, isSuccess = true, note = "") }
                    }
                }
                .onFailure { e ->
                    val errorMsg = e.message ?: "Unknown error"
                    updateState { copy(isLoading = false, error = errorMsg) }
                    sendEffect(StatusEffect.ShowError(errorMsg))
                }
        }
    }

    private fun finishStatus(id: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            statusRepository.updateStatusEndTime(id, System.currentTimeMillis())
                .onSuccess {
                    updateState { copy(isLoading = false, isSuccess = true, activeStatus = null) }
                }
                .onFailure { e ->
                    val errorMsg = e.message ?: "Unknown error"
                    updateState { copy(isLoading = false, error = errorMsg) }
                    sendEffect(StatusEffect.ShowError(errorMsg))
                }
        }
    }

    private fun dismissDialog() {
        updateState { copy(isSuccess = false) }
        // Не виходимо, якщо просто оновили статус
    }
}
