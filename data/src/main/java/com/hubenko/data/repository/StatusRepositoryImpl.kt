package com.hubenko.data.repository

import android.content.Context
import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeStatusDao
import com.hubenko.data.local.entity.EmployeeStatusEntity
import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.domain.repository.StatusRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StatusRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dao: EmployeeStatusDao,
    private val firestore: FirebaseFirestore
) : StatusRepository {

    override fun getAllStatuses(): Flow<List<EmployeeStatus>> {
        return dao.getAllStatuses().map { list -> list.map { it.toDomain() } }
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
        
        // Trigger sync via implicit broadcast or reflection
        // Broadcast is cleaner than reflection for decoupling
        val intent = Intent("com.hubenko.firestoreapp.SYNC_STATUSES")
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)
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

    private fun EmployeeStatusEntity.toDomain() = EmployeeStatus(
        id = id,
        employeeId = employeeId,
        status = status,
        timestamp = timestamp,
        isSynced = isSynced
    )
}
