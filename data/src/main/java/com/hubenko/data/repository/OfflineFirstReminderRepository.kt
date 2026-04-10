package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.ReminderSettingsDao
import com.hubenko.data.mapper.toDomain
import com.hubenko.data.mapper.toEntity
import com.hubenko.data.remote.document.ReminderSettingsDocument
import com.hubenko.data.util.firestoreSafeCall
import com.hubenko.domain.error.DataError
import com.hubenko.domain.manager.ReminderManager
import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.repository.ReminderRepository
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OfflineFirstReminderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dao: ReminderSettingsDao,
    private val reminderManager: ReminderManager
) : ReminderRepository {

    override fun getReminderSettings(employeeId: String): Flow<ReminderSettings> {
        return dao.getSettings(employeeId).map { entity ->
            entity?.toDomain() ?: ReminderSettings(employeeId = employeeId)
        }
    }

    override suspend fun saveReminderSettings(settings: ReminderSettings): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            val document = ReminderSettingsDocument(
                employeeId = settings.employeeId,
                morningEnabled = settings.morningEnabled,
                morningStartTime = settings.morningStartTime,
                morningEndTime = settings.morningEndTime,
                morningIntervalMinutes = settings.morningIntervalMinutes,
                eveningEnabled = settings.eveningEnabled,
                eveningStartTime = settings.eveningStartTime,
                eveningEndTime = settings.eveningEndTime,
                eveningIntervalMinutes = settings.eveningIntervalMinutes,
                daysOfWeek = settings.daysOfWeek.map { it.toLong() }
            )
            firestore.collection("reminder_settings")
                .document(settings.employeeId)
                .set(document)
                .await()
            updateLocalCache(settings)
        }

    override suspend fun updateLocalCache(settings: ReminderSettings) {
        dao.insertSettings(settings.toEntity())
        reminderManager.scheduleReminder(settings)
    }

    override suspend fun getLocalSettings(employeeId: String): ReminderSettings? {
        return dao.getSettingsSync(employeeId)?.toDomain()
    }
}
