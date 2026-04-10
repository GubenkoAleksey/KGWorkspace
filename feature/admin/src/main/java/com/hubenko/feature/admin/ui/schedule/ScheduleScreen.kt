package com.hubenko.feature.admin.ui.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.core.presentation.ObserveAsEvents

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    onNavigateToReminderSettings: (String) -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is ScheduleEffect.NavigateToReminderSettings -> onNavigateToReminderSettings(effect.employeeId)
        }
    }

    ScheduleContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}
