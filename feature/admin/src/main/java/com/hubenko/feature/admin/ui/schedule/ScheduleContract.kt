package com.hubenko.feature.admin.ui.schedule

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.Employee

data class ScheduleState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false
) : ViewState

sealed class ScheduleIntent : ViewIntent {
    data object LoadData : ScheduleIntent()
    data class OnEmployeeClick(val employeeId: String) : ScheduleIntent()
}

sealed class ScheduleEffect : ViewSideEffect {
    data class NavigateToReminderSettings(val employeeId: String) : ScheduleEffect()
}

