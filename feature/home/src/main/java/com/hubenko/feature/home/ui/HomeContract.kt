package com.hubenko.feature.home.ui

import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState

data class HomeState(
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true
) : ViewState

sealed interface HomeIntent : ViewIntent {
    data object LoadAdminStatus : HomeIntent
    data object OnAdminPanelClick : HomeIntent
    data object OnSendStatusClick : HomeIntent
    data object OnMyStatusesClick : HomeIntent
    data object OnLogoutClick : HomeIntent
}

sealed interface HomeEffect : ViewSideEffect {
    data class ShowSnackbar(val message: UiText) : HomeEffect
    data object NavigateToStatus : HomeEffect
    data object NavigateToAdmin : HomeEffect
    data object NavigateToAuth : HomeEffect
    data class NavigateToMyStatuses(val employeeId: String) : HomeEffect
}
