package com.hubenko.feature.status.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.domain.model.StatusType

data class StatusState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val note: String = "",
    val showConfirmDialog: Boolean = false,
    val pendingStatus: String? = null,
    val activeStatus: EmployeeStatus? = null,
    val statusTypes: List<StatusType> = emptyList()
) : ViewState

sealed class StatusIntent : ViewIntent {
    object LoadActiveStatus : StatusIntent()
    data class SubmitStatusClick(val status: String) : StatusIntent()
    object ConfirmSubmit : StatusIntent()
    object DismissConfirmDialog : StatusIntent()
    data class UpdateNote(val note: String) : StatusIntent()
    object DismissDialog : StatusIntent()
    object OnBackClick : StatusIntent()
}

sealed class StatusEffect : ViewSideEffect {
    object NavigateBack : StatusEffect()
    data class ShowError(val message: String) : StatusEffect()
}
