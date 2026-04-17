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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OfflineFirstReminderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dao: ReminderSettingsDao,
    private val reminderManager: ReminderManager
) : ReminderRepository {

    private val remindersCollection = firestore.collection("reminder_settings")

    override fun getReminderSettings(employeeId: String): Flow<ReminderSettings> {
        return dao.getSettings(employeeId).map { entity ->
            entity?.toDomain() ?: ReminderSettings(employeeId = employeeId)
        }
    }

    override fun getAllReminderSettings(): Flow<List<ReminderSettings>> = channelFlow {
        launch { syncRemindersFromFirestore() }
        dao.getAllSettings()
            .map { entities -> entities.map { it.toDomain() } }
            .collect { send(it) }
    }

    private suspend fun syncRemindersFromFirestore() {
        try {
            val snapshot = remindersCollection.get().await()
            snapshot.documents.forEach { doc ->
                val document = doc.toObject(ReminderSettingsDocument::class.java) ?: return@forEach
                val employeeId = document.employeeId.ifEmpty { doc.id }
                val settings = ReminderSettings(
                    employeeId = employeeId,
                    morningEnabled = document.morningEnabled,
                    morningStartTime = document.morningStartTime,
                    morningEndTime = document.morningEndTime,
                    morningIntervalMinutes = document.morningIntervalMinutes,
                    eveningEnabled = document.eveningEnabled,
                    eveningStartTime = document.eveningStartTime,
                    eveningEndTime = document.eveningEndTime,
                    eveningIntervalMinutes = document.eveningIntervalMinutes,
                    daysOfWeek = document.daysOfWeek.map { it.toInt() }
                )
                dao.insertSettings(settings.toEntity())
            }
        } catch (_: Exception) {
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
            remindersCollection
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
