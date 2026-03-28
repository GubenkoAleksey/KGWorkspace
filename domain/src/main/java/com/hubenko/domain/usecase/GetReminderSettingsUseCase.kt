package com.hubenko.domain.usecase

import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReminderSettingsUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    operator fun invoke(employeeId: String): Flow<ReminderSettings> {
        return repository.getReminderSettings(employeeId)
    }
}
