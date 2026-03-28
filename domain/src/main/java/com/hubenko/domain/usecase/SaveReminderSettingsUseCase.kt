package com.hubenko.domain.usecase

import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.repository.ReminderRepository
import javax.inject.Inject

class SaveReminderSettingsUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(settings: ReminderSettings): Result<Unit> {
        return repository.saveReminderSettings(settings)
    }
}
