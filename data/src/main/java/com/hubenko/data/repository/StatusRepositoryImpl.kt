package com.hubenko.data.repository

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
                    timestamp = doc.getLong("timestamp") ?: 0L,
                    isSynced = true
                )
                dao.insertStatus(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun saveStatusLocally(employeeId: String, status: String) {
        val entity = EmployeeStatusEntity(
            id = UUID.randomUUID().toString(),
            employeeId = employeeId,
            status = status,
            timestamp = System.currentTimeMillis(),
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
                    "timestamp" to status.timestamp
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
}
