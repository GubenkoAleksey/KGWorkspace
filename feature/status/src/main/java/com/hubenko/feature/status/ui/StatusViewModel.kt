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
            is StatusIntent.SubmitStatus -> submitStatus(intent.status)
            is StatusIntent.DismissDialog -> dismissDialog()
            is StatusIntent.OnBackClick -> sendEffect(StatusEffect.NavigateBack)
        }
    }

    private fun submitStatus(status: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            
            submitStatusUseCase(status)
                .onSuccess {
                    updateState { copy(isLoading = false, isSuccess = true) }
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
