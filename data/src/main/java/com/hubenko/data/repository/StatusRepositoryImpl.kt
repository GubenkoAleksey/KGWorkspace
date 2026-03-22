package com.hubenko.data.repository

import android.util.Log
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeStatusDao
import com.hubenko.data.local.entity.EmployeeStatusEntity
import com.hubenko.data.mapper.toDomain
import com.hubenko.data.worker.SyncWorker
import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.domain.repository.StatusRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StatusRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dao: EmployeeStatusDao,
    private val firestore: FirebaseFirestore
) : StatusRepository {

    override fun getAllStatuses(): Flow<List<EmployeeStatus>> {
        return dao.getAllStatusesWithDetails()
            .map { list -> list.map { it.toDomain() } }
            .onStart { fetchStatusesFromRemote() }
    }

    override suspend fun fetchStatusesFromRemote() {
        try {
            val snapshot = firestore.collection("employee_statuses").get().await()
            snapshot.documents.forEach { doc ->
                val entity = EmployeeStatusEntity(
                    id = doc.id,
                    employeeId = doc.getString("employeeId") ?: "",
                    status = doc.getString("status") ?: "",
                    note = doc.getString("note"),
                    startTime = doc.getLong("startTime") ?: 0L,
                    endTime = doc.getLong("endTime"),
                    isSynced = true
                )
                dao.insertStatus(entity)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch statuses from remote", e)
        }
    }

    override suspend fun saveStatusLocally(employeeId: String, status: String, note: String?) {
        val entity = EmployeeStatusEntity(
            id = UUID.randomUUID().toString(),
            employeeId = employeeId,
            status = status,
            note = note,
            startTime = System.currentTimeMillis(),
            endTime = null,
            isSynced = false
        )
        dao.insertStatus(entity)
        triggerSync()
    }

    override suspend fun getUnsyncedStatuses(): List<EmployeeStatus> {
        return dao.getUnsyncedStatuses().map { it.toDomain() }
    }

    override suspend fun syncStatuses(statuses: List<EmployeeStatus>): Result<Unit> {
        return try {
            val batch = firestore.batch()
            statuses.forEach { status ->
                val docRef = firestore.collection("employee_statuses").document(status.id)
                val map = hashMapOf(
                    "id" to status.id,
                    "employeeId" to status.employeeId,
                    "status" to status.status,
                    "startTime" to status.startTime
                )
                status.note?.let { map["note"] = it }
                status.endTime?.let { map["endTime"] = it }
                
                batch.set(docRef, map)
            }
            batch.commit().await()
            dao.markAsSynced(statuses.map { it.id })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun triggerSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "SyncStatusesWork",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    override suspend fun deleteAllStatuses(): Result<Unit> {
        return try {
            val snapshot = firestore.collection("employee_statuses").get().await()
            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            dao.deleteAllStatuses()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActiveStatus(employeeId: String): EmployeeStatus? {
        return dao.getActiveStatus(employeeId)?.toDomain()
    }

    override suspend fun updateStatusEndTime(id: String, endTime: Long): Result<Unit> {
        return try {
            dao.updateEndTime(id, endTime)
            triggerSync()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        private const val TAG = "StatusRepositoryImpl"
    }
}
