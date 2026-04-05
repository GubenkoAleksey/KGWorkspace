package com.hubenko.feature.admin.ui.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

/**
 * Stateful Composable для екрана розкладу сповіщень.
 *
 * @param viewModel ViewModel екрана, ін'єктується через Hilt.
 * @param onNavigateToReminderSettings Callback навігації до налаштувань нагадувань конкретного співробітника.
 * @param onBackClick Callback для повернення на Dashboard.
 */
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateToReminderSettings: (String) -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ScheduleEffect.NavigateToReminderSettings -> {
                    onNavigateToReminderSettings(effect.employeeId)
                }
            }
        }
    }

    ScheduleContent(
        state = state,
        onIntent = viewModel::onIntent,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle
    )
}

