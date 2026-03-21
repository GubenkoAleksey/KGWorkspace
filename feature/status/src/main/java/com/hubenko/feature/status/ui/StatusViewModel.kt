package com.hubenko.feature.status.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.usecase.SubmitStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val submitStatusUseCase: SubmitStatusUseCase
) : BaseViewModel<StatusState, StatusIntent, StatusEffect>(StatusState()) {

    override fun onIntent(intent: StatusIntent) {
        when (intent) {
            is StatusIntent.SubmitStatusClick -> {
                updateState { copy(showConfirmDialog = true, pendingStatus = intent.status) }
            }
            is StatusIntent.ConfirmSubmit -> {
                val pending = viewState.value.pendingStatus
                if (pending != null) {
                    val currentNote = viewState.value.note
                    updateState { copy(showConfirmDialog = false, pendingStatus = null) }
                    submitStatus(pending, currentNote)
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

    private fun submitStatus(status: String, note: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            
            val noteToSubmit = note.trim().takeIf { it.isNotEmpty() }
            
            submitStatusUseCase(status, noteToSubmit)
                .onSuccess {
                    updateState { copy(isLoading = false, isSuccess = true, note = "") }
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
        // Navigate back after success
        sendEffect(StatusEffect.NavigateBack)
    }
}
