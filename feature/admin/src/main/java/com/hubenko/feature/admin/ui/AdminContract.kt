package com.hubenko.feature.admin.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState

data class AdminState(
    val selectedTab: AdminTab = AdminTab.DASHBOARD
) : ViewState

enum class AdminTab(val title: String) {
    DASHBOARD("Панель адміністратора"),
    EMPLOYEES("Керування працівниками"),
    SCHEDULE("Розклад сповіщень"),
    STATUSES("Статуси працівників")
}

sealed class AdminIntent : ViewIntent {
    data class OnTabSelected(val tab: AdminTab) : AdminIntent()
    data object OnBackClick : AdminIntent()
}

sealed class AdminEffect : ViewSideEffect {
    data object NavigateBack : AdminEffect()
}
