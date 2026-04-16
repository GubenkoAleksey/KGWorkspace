package com.hubenko.feature.admin.ui.schedule

import androidx.compose.runtime.Stable
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState

@Stable
data class ScheduleState(
    val employees: List<ScheduleEmployeeUi> = emptyList(),
    val isLoading: Boolean = false
) : ViewState

sealed interface ScheduleIntent : ViewIntent {
    data object LoadData : ScheduleIntent
    data class OnEmployeeClick(val employeeId: String) : ScheduleIntent
}

sealed interface ScheduleEffect : ViewSideEffect {
    data class NavigateToReminderSettings(val employeeId: String) : ScheduleEffect
}
