package com.hubenko.feature.home.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState

data class HomeState(
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true,
    val isDarkTheme: Boolean = false
) : ViewState

sealed class HomeIntent : ViewIntent {
    data object LoadAdminStatus : HomeIntent()
    data object OnAdminPanelClick : HomeIntent()
    data object OnSendStatusClick : HomeIntent()
    data object OnLogoutClick : HomeIntent()
    data object OnTestNotificationClick : HomeIntent()
    data object OnThemeToggle : HomeIntent()
}

sealed class HomeEffect : ViewSideEffect {
    data class ShowToast(val message: String) : HomeEffect()
    data object NavigateToStatus : HomeEffect()
    data object NavigateToAdmin : HomeEffect()
    data object NavigateToAuth : HomeEffect()
}
