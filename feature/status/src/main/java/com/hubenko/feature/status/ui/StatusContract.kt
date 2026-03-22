package com.hubenko.feature.status.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.EmployeeStatus

const val NOTE_MAX_LENGTH = 300

data class StatusState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val note: String = "",
    val showConfirmDialog: Boolean = false,
    val pendingStatus: String? = null,
    val activeStatus: EmployeeStatus? = null
) : ViewState

sealed class StatusIntent : ViewIntent {
    data object LoadActiveStatus : StatusIntent()
    data class SubmitStatusClick(val status: String) : StatusIntent()
    data object ConfirmSubmit : StatusIntent()
    data object DismissConfirmDialog : StatusIntent()
    data class UpdateNote(val note: String) : StatusIntent()
    data object DismissDialog : StatusIntent()
    data object OnBackClick : StatusIntent()
}

sealed class StatusEffect : ViewSideEffect {
    data object NavigateBack : StatusEffect()
    data class ShowError(val message: String) : StatusEffect()
}
