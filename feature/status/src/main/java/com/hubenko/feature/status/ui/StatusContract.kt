package com.hubenko.feature.status.ui

import androidx.compose.runtime.Stable
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState
import com.hubenko.feature.status.ui.model.EmployeeStatusUi
import com.hubenko.feature.status.ui.model.StatusTypeUi

@Stable
data class StatusState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val note: String = "",
    val showConfirmDialog: Boolean = false,
    val pendingStatus: String? = null,
    val activeStatus: EmployeeStatusUi? = null,
    val statusTypes: List<StatusTypeUi> = emptyList()
) : ViewState

sealed interface StatusIntent : ViewIntent {
    data object LoadActiveStatus : StatusIntent
    data class SubmitStatusClick(val status: String) : StatusIntent
    data object ConfirmSubmit : StatusIntent
    data object DismissConfirmDialog : StatusIntent
    data class UpdateNote(val note: String) : StatusIntent
    data object OnBackClick : StatusIntent
}

sealed interface StatusEffect : ViewSideEffect {
    data object NavigateBack : StatusEffect
    data class ShowSnackbar(val message: UiText) : StatusEffect
}
