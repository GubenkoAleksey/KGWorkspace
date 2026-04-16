package com.hubenko.domain.usecase

import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllReminderSettingsUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    operator fun invoke(): Flow<List<ReminderSettings>> =
        repository.getAllReminderSettings()
}
