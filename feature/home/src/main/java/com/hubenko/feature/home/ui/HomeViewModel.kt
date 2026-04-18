package com.hubenko.feature.home.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.UiText
import com.hubenko.feature.home.R
import com.hubenko.domain.usecase.CheckAdminStatusUseCase
import com.hubenko.domain.usecase.GetCurrentUserIdUseCase
import com.hubenko.domain.usecase.LogoutUseCase
import com.hubenko.domain.usecase.SyncMyRemindersUseCase
import com.hubenko.domain.util.onFailure
import com.hubenko.domain.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkAdminStatusUseCase: CheckAdminStatusUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val syncMyRemindersUseCase: SyncMyRemindersUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    init {
        onIntent(HomeIntent.LoadAdminStatus)
        syncReminders()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadAdminStatus -> loadAdminStatus()
            is HomeIntent.OnAdminPanelClick -> sendEffect(HomeEffect.NavigateToAdmin)
            is HomeIntent.OnSendStatusClick -> sendEffect(HomeEffect.NavigateToStatus)
            is HomeIntent.OnMyStatusesClick -> navigateToMyStatuses()
            is HomeIntent.OnLogoutClick -> {
                logoutUseCase()
                sendEffect(HomeEffect.NavigateToAuth)
            }
        }
    }

    private fun loadAdminStatus() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            checkAdminStatusUseCase()
                .onSuccess { isAdmin ->
                    updateState { copy(isAdmin = isAdmin, isLoading = false) }
                }
                .onFailure {
                    updateState { copy(isLoading = false) }
                    sendEffect(HomeEffect.ShowSnackbar(UiText.StringResource(R.string.error_check_status_failed)))
                }
        }
    }

    private fun navigateToMyStatuses() {
        val userId = getCurrentUserIdUseCase() ?: return
        sendEffect(HomeEffect.NavigateToMyStatuses(userId))
    }

    private fun syncReminders() {
        viewModelScope.launch {
            try {
                syncMyRemindersUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Not critical if fails, will use local settings
            }
        }
    }
}
