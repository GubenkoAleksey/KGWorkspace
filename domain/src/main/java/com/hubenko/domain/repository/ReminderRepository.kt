package com.hubenko.domain.repository

import com.hubenko.domain.model.ReminderSettings
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getReminderSettings(employeeId: String): Flow<ReminderSettings>
    suspend fun saveReminderSettings(settings: ReminderSettings): Result<Unit>
    suspend fun updateLocalCache(settings: ReminderSettings)
    suspend fun getLocalSettings(employeeId: String): ReminderSettings?
}
