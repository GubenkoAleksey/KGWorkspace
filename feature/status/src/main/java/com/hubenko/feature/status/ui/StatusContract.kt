package com.hubenko.feature.status.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState

data class StatusState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) : ViewState

sealed class StatusIntent : ViewIntent {
    data class SubmitStatus(val status: String) : StatusIntent()
    object DismissDialog : StatusIntent()
    object OnBackClick : StatusIntent()
}

sealed class StatusEffect : ViewSideEffect {
    object NavigateBack : StatusEffect()
    data class ShowError(val message: String) : StatusEffect()
}
