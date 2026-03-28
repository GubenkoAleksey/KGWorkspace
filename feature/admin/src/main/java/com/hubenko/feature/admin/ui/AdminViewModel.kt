package com.hubenko.feature.admin.ui

import com.hubenko.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Тонкий координатор навігації панелі адміністратора.
 * Відповідає виключно за перемикання між вкладками та вихід з панелі.
 */
@HiltViewModel
class AdminViewModel @Inject constructor() :
    BaseViewModel<AdminState, AdminIntent, AdminEffect>(AdminState()) {

    override fun onIntent(intent: AdminIntent) {
        when (intent) {
            is AdminIntent.OnTabSelected -> updateState { copy(selectedTab = intent.tab) }
            is AdminIntent.OnBackClick -> handleBackClick()
        }
    }

    private fun handleBackClick() {
        if (viewState.value.selectedTab == AdminTab.DASHBOARD) {
            sendEffect(AdminEffect.NavigateBack)
        } else {
            updateState { copy(selectedTab = AdminTab.DASHBOARD) }
        }
    }
}
