package com.hubenko.feature.home.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.repository.SettingsRepository
import com.hubenko.domain.usecase.CheckAdminStatusUseCase
import com.hubenko.domain.usecase.LogoutUseCase
import com.hubenko.domain.usecase.SyncMyRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkAdminStatusUseCase: CheckAdminStatusUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val syncMyRemindersUseCase: SyncMyRemindersUseCase,
    private val settingsRepository: SettingsRepository
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    init {
        onIntent(HomeIntent.LoadAdminStatus)
        syncReminders()
        observeTheme()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadAdminStatus -> loadAdminStatus()
            is HomeIntent.OnAdminPanelClick -> {
                sendEffect(HomeEffect.NavigateToAdmin)
            }
            is HomeIntent.OnSendStatusClick -> {
                sendEffect(HomeEffect.NavigateToStatus)
            }
            is HomeIntent.OnLogoutClick -> {
                logoutUseCase()
                sendEffect(HomeEffect.NavigateToAuth)
            }
            is HomeIntent.OnThemeToggle -> {
                viewModelScope.launch {
                    settingsRepository.toggleTheme()
                }
            }
        }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            settingsRepository.isDarkTheme().collect { isDark ->
                updateState { copy(isDarkTheme = isDark) }
            }
        }
    }

    private fun loadAdminStatus() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val isAdmin = checkAdminStatusUseCase()
                updateState { copy(isAdmin = isAdmin, isLoading = false) }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendEffect(HomeEffect.ShowToast("Помилка перевірки статусу"))
            }
        }
    }

    private fun syncReminders() {
        viewModelScope.launch {
            try {
                syncMyRemindersUseCase()
            } catch (e: Exception) {
                // Not critical if fails, will use local settings
            }
        }
    }
}
