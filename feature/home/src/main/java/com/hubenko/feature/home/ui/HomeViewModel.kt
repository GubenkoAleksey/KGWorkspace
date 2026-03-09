package com.hubenko.feature.home.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.usecase.CheckAdminStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkAdminStatusUseCase: CheckAdminStatusUseCase
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    init {
        onIntent(HomeIntent.LoadAdminStatus)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadAdminStatus -> loadAdminStatus()
            is HomeIntent.OnAdminPanelClick -> {
                sendEffect(HomeEffect.ShowToast("Панель в розробці..."))
            }
            is HomeIntent.OnSendStatusClick -> {
                sendEffect(HomeEffect.NavigateToStatus)
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
}
