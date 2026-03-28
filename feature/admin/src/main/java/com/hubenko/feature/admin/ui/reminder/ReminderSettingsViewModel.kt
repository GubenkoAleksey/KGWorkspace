package com.hubenko.feature.admin.ui.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.usecase.GetReminderSettingsUseCase
import com.hubenko.domain.usecase.SaveReminderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderSettingsState(
    val settings: ReminderSettings = ReminderSettings(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

sealed class ReminderSettingsIntent {
    data class LoadSettings(val employeeId: String) : ReminderSettingsIntent()
    data class UpdateSettings(val settings: ReminderSettings) : ReminderSettingsIntent()
    object SaveSettings : ReminderSettingsIntent()
}

@HiltViewModel
class ReminderSettingsViewModel @Inject constructor(
    private val getReminderSettingsUseCase: GetReminderSettingsUseCase,
    private val saveReminderSettingsUseCase: SaveReminderSettingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReminderSettingsState())
    val state: StateFlow<ReminderSettingsState> = _state.asStateFlow()

    fun onIntent(intent: ReminderSettingsIntent) {
        when (intent) {
            is ReminderSettingsIntent.LoadSettings -> loadSettings(intent.employeeId)
            is ReminderSettingsIntent.UpdateSettings -> {
                _state.update { it.copy(settings = intent.settings) }
            }
            ReminderSettingsIntent.SaveSettings -> saveSettings()
        }
    }

    private fun loadSettings(employeeId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getReminderSettingsUseCase(employeeId)
                .catch { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
                .collect { settings ->
                    _state.update { it.copy(settings = settings, isLoading = false) }
                }
        }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = saveReminderSettingsUseCase(_state.value.settings)
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Failed to save") }
            }
        }
    }
}
