package com.hubenko.feature.admin.ui

import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState

data class AdminState(
    val selectedTab: AdminTab = AdminTab.DASHBOARD
) : ViewState

enum class AdminTab(val title: String) {
    DASHBOARD("Панель адміністратора"),
    EMPLOYEES("Керування працівниками"),
    SCHEDULE("Розклад сповіщень"),
    STATUSES("Статуси працівників"),
    DIRECTORIES("Довідники")
}

sealed interface AdminIntent : ViewIntent {
    data class OnTabSelected(val tab: AdminTab) : AdminIntent
    data object OnBackClick : AdminIntent
}

sealed interface AdminEffect : ViewSideEffect {
    data object NavigateBack : AdminEffect
}
