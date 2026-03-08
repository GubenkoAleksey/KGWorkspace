package com.hubenko.firestoreapp.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.firestoreapp.data.local.EmployeeStatusDao
import com.hubenko.firestoreapp.data.local.EmployeeStatusEntity
import com.hubenko.firestoreapp.worker.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StatusRepository(
    private val context: Context,
    private val dao: EmployeeStatusDao,
    private val firestore: FirebaseFirestore
) {

    /**
     * Get all statuses as a reactive stream from local DB
     */
    fun getAllStatuses(): Flow<List<EmployeeStatusEntity>> {
        return dao.getAllStatuses()
    }

    /**
     * Submit a new status (Office, Remote, Sick).
     * Instantly saved to local DB.
     * Enqueues a sync job to push it to Firestore when network is available.
     */
    suspend fun saveStatusLocally(employeeId: String, status: String) {
        val entity = EmployeeStatusEntity(
            id = UUID.randomUUID().toString(),
            employeeId = employeeId,
            status = status,
            timestamp = System.currentTimeMillis(),
            isSynced = false
        )
        dao.insertStatus(entity)
        // Note: the background synchronization (via WorkManager) will pick up un-synced entries.
        SyncManager.enqueueSyncWork(context)
    }

    /**
     * Get un-synced statuses for background worker.
     */
    suspend fun getUnsyncedStatuses(): List<EmployeeStatusEntity> {
        return dao.getUnsyncedStatuses()
    }

    /**
     * Syncs a specific list of statuses to Firestore and updates their synced state locally.
     */
    suspend fun syncStatuses(statuses: List<EmployeeStatusEntity>): Result<Unit> {
        return try {
            val batch = firestore.batch()
            statuses.forEach { entity ->
                val docRef = firestore.collection("employee_statuses").document(entity.id)
                val map = hashMapOf(
                    "id" to entity.id,
                    "employeeId" to entity.employeeId,
                    "status" to entity.status,
                    "timestamp" to entity.timestamp
                )
                batch.set(docRef, map)
            }
            batch.commit().await()
            dao.markAsSynced(statuses.map { it.id })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
