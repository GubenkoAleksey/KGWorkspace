package com.hubenko.feature.home.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState

data class HomeState(
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true
) : ViewState

sealed class HomeIntent : ViewIntent {
    object LoadAdminStatus : HomeIntent()
    object OnAdminPanelClick : HomeIntent()
    object OnSendStatusClick : HomeIntent()
}

sealed class HomeEffect : ViewSideEffect {
    data class ShowToast(val message: String) : HomeEffect()
    object NavigateToStatus : HomeEffect()
    object NavigateToAdmin : HomeEffect()
}
