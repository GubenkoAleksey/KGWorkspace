package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.repository.ReminderRepository
import com.hubenko.domain.util.EmptyResult
import javax.inject.Inject

class SaveReminderSettingsUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(settings: ReminderSettings): EmptyResult<DataError.Firestore> =
        repository.saveReminderSettings(settings)
}
