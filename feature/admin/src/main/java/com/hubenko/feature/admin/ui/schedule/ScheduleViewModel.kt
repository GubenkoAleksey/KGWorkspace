package com.hubenko.feature.admin.ui.schedule

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.feature.admin.ui.model.toEmployeeUi
import com.hubenko.domain.usecase.GetAllEmployeesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getAllEmployeesUseCase: GetAllEmployeesUseCase
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
        viewModelScope.launch {
            getAllEmployeesUseCase().collectLatest { list ->
                updateState { copy(employees = list.map { it.toEmployeeUi() }, isLoading = false) }
            }
        }
    }
}
