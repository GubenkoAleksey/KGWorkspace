package com.hubenko.feature.admin.ui.reminder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.navigation.ReminderSettingsRoute
import com.hubenko.feature.admin.ui.model.ReminderSettingsUi
import com.hubenko.feature.admin.ui.model.toDomain
import com.hubenko.feature.admin.ui.model.toReminderSettingsUi
import com.hubenko.domain.usecase.GetReminderSettingsUseCase
import com.hubenko.domain.usecase.SaveReminderSettingsUseCase
import com.hubenko.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderSettingsState(
    val settings: ReminderSettingsUi = ReminderSettingsUi(),
    val isLoading: Boolean = false,
    val error: UiText? = null
) : ViewState

sealed interface ReminderSettingsIntent : ViewIntent {
    data class UpdateSettings(val settings: ReminderSettingsUi) : ReminderSettingsIntent
    data object SaveSettings : ReminderSettingsIntent
}

sealed interface ReminderSettingsEffect : ViewSideEffect {
    data object NavigateBack : ReminderSettingsEffect
    data class ShowSnackbar(val message: UiText) : ReminderSettingsEffect
}

@HiltViewModel
class ReminderSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getReminderSettingsUseCase: GetReminderSettingsUseCase,
    private val saveReminderSettingsUseCase: SaveReminderSettingsUseCase
) : BaseViewModel<ReminderSettingsState, ReminderSettingsIntent, ReminderSettingsEffect>(
    ReminderSettingsState()
) {
    private val employeeId = savedStateHandle.toRoute<ReminderSettingsRoute>().employeeId

    init {
        loadSettings()
    }

    override fun onIntent(intent: ReminderSettingsIntent) {
        when (intent) {
            is ReminderSettingsIntent.UpdateSettings -> updateState { copy(settings = intent.settings) }
            ReminderSettingsIntent.SaveSettings -> saveSettings()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            getReminderSettingsUseCase(employeeId)
                .catch { e ->
                    updateState { copy(error = UiText.DynamicString(e.message ?: ""), isLoading = false) }
                }
                .collect { settings ->
                    updateState { copy(settings = settings.toReminderSettingsUi(), isLoading = false) }
                }
        }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            when (saveReminderSettingsUseCase(viewState.value.settings.toDomain())) {
                is Result.Success -> {
                    updateState { copy(isLoading = false) }
                    sendEffect(ReminderSettingsEffect.ShowSnackbar(UiText.StringResource(R.string.success_settings_saved)))
                    sendEffect(ReminderSettingsEffect.NavigateBack)
                }
                is Result.Error -> {
                    updateState { copy(isLoading = false, error = UiText.StringResource(com.hubenko.core.R.string.error_unknown)) }
                }
            }
        }
    }
}
