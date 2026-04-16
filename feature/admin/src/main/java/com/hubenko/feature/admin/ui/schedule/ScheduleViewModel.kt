package com.hubenko.feature.admin.ui.schedule

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.usecase.GetAllEmployeesUseCase
import com.hubenko.domain.usecase.GetAllReminderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getAllEmployeesUseCase: GetAllEmployeesUseCase,
    private val getAllReminderSettingsUseCase: GetAllReminderSettingsUseCase
) : BaseViewModel<ScheduleState, ScheduleIntent, ScheduleEffect>(ScheduleState()) {

    init {
        onIntent(ScheduleIntent.LoadData)
    }

    override fun onIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.LoadData -> loadData()
            is ScheduleIntent.OnEmployeeClick -> {
                sendEffect(ScheduleEffect.NavigateToReminderSettings(intent.employeeId))
            }
        }
    }

    private fun loadData() {
        updateState { copy(isLoading = true) }
        combine(
            getAllEmployeesUseCase(),
            getAllReminderSettingsUseCase()
        ) { employees, settings ->
            val settingsMap = settings.associateBy { it.employeeId }
            employees.map { employee ->
                employee.toScheduleEmployeeUi(
                    settings = settingsMap[employee.id] ?: ReminderSettings(employeeId = employee.id)
                )
            }
        }.onEach { employees ->
            updateState { copy(employees = employees, isLoading = false) }
        }.launchIn(viewModelScope)
    }
}
