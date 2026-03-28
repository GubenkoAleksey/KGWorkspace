package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.ReminderSettingsDao
import com.hubenko.data.local.entity.ReminderSettingsEntity
import com.hubenko.domain.manager.ReminderManager
import com.hubenko.domain.model.ReminderSettings
import com.hubenko.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dao: ReminderSettingsDao,
    private val reminderManager: ReminderManager
) : ReminderRepository {

    override fun getReminderSettings(employeeId: String): Flow<ReminderSettings> {
        return dao.getSettings(employeeId).map { entity ->
            entity?.toDomain() ?: ReminderSettings(employeeId = employeeId)
        }
    }

    override suspend fun saveReminderSettings(settings: ReminderSettings): Result<Unit> {
        return try {
            val map = hashMapOf(
                "employeeId" to settings.employeeId,
                "morningEnabled" to settings.morningEnabled,
                "morningStartTime" to settings.morningStartTime,
                "morningEndTime" to settings.morningEndTime,
                "morningIntervalMinutes" to settings.morningIntervalMinutes,
                "eveningEnabled" to settings.eveningEnabled,
                "eveningStartTime" to settings.eveningStartTime,
                "eveningEndTime" to settings.eveningEndTime,
                "eveningIntervalMinutes" to settings.eveningIntervalMinutes,
                "daysOfWeek" to settings.daysOfWeek
            )
            firestore.collection("reminder_settings")
                .document(settings.employeeId)
                .set(map)
                .await()
            
            updateLocalCache(settings)
            
            // Schedule the alarm immediately after saving
            reminderManager.scheduleReminder(settings)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLocalCache(settings: ReminderSettings) {
        dao.insertSettings(settings.toEntity())
        // Також оновлюємо таймери для локального пристрою користувача при завантаженні з Firestore
        reminderManager.scheduleReminder(settings)
    }

    override suspend fun getLocalSettings(employeeId: String): ReminderSettings? {
        return dao.getSettingsSync(employeeId)?.toDomain()
    }

    private fun ReminderSettingsEntity.toDomain() = ReminderSettings(
        employeeId = employeeId,
        morningEnabled = morningEnabled,
        morningStartTime = morningStartTime,
        morningEndTime = morningEndTime,
        morningIntervalMinutes = morningIntervalMinutes,
        eveningEnabled = eveningEnabled,
        eveningStartTime = eveningStartTime,
        eveningEndTime = eveningEndTime,
        eveningIntervalMinutes = eveningIntervalMinutes,
        daysOfWeek = daysOfWeek.split(",").mapNotNull { it.toIntOrNull() }
    )

    private fun ReminderSettings.toEntity() = ReminderSettingsEntity(
        employeeId = employeeId,
        morningEnabled = morningEnabled,
        morningStartTime = morningStartTime,
        morningEndTime = morningEndTime,
        morningIntervalMinutes = morningIntervalMinutes,
        eveningEnabled = eveningEnabled,
        eveningStartTime = eveningStartTime,
        eveningEndTime = eveningEndTime,
        eveningIntervalMinutes = eveningIntervalMinutes,
        daysOfWeek = daysOfWeek.joinToString(",")
    )
}
