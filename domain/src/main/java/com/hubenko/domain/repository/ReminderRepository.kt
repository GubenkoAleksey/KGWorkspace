package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getReminderSettings(employeeId: String): Flow<ReminderSettings>
    suspend fun saveReminderSettings(settings: ReminderSettings): EmptyResult<DataError.Firestore>
    suspend fun updateLocalCache(settings: ReminderSettings)
    suspend fun getLocalSettings(employeeId: String): ReminderSettings?
}
