package com.hubenko.feature.admin.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.core.presentation.ObserveAsEvents

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReminderSettings: (String) -> Unit,
    onNavigateToRegisterEmployee: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.onIntent(AdminIntent.OnBackClick)
    }

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is AdminEffect.NavigateBack -> onNavigateBack()
        }
    }

    AdminContent(
        state = state,
        onTabSelected = { viewModel.onIntent(AdminIntent.OnTabSelected(it)) },
        onNavigateToReminderSettings = onNavigateToReminderSettings,
        onNavigateToRegisterEmployee = onNavigateToRegisterEmployee
    )

}
