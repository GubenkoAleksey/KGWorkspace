package com.hubenko.feature.admin.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

/**
 * Stateful Composable для панелі адміністратора.
 * Обробляє навігаційні ефекти та передає callbacks до [AdminContent].
 */
@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToReminderSettings: (String) -> Unit,
    onNavigateToRegisterEmployee: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.onIntent(AdminIntent.OnBackClick)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AdminEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    AdminContent(
        state = state,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        onTabSelected = { viewModel.onIntent(AdminIntent.OnTabSelected(it)) },
        onNavigateToReminderSettings = onNavigateToReminderSettings,
        onNavigateToRegisterEmployee = onNavigateToRegisterEmployee
    )
}
