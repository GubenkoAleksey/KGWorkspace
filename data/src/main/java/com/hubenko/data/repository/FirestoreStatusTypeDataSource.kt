package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeStatusDao
import com.hubenko.data.local.dao.StatusTypeDao
import com.hubenko.data.mapper.toStatusType
import com.hubenko.data.mapper.toStatusTypeEntity
import com.hubenko.data.remote.document.StatusTypeDocument
import com.hubenko.data.util.firestoreSafeCall
import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.StatusType
import com.hubenko.domain.repository.StatusTypeDataSource
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreStatusTypeDataSource @Inject constructor(
    private val dao: StatusTypeDao,
    private val employeeStatusDao: EmployeeStatusDao,
    private val firestore: FirebaseFirestore
) : StatusTypeDataSource {

    private val collection = firestore.collection("status_types")

    override fun getStatusTypes(): Flow<List<StatusType>> = callbackFlow {
        val scope = this
        val listener = collection
            .orderBy("label")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                if (snapshot.isEmpty) {
                    scope.launch { seedDefaults() }
                } else {
                    val types = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(StatusTypeDocument::class.java)?.toStatusType()
                    }
                    scope.launch { dao.insertAll(types.map { StatusTypeDocument(type = it.type, label = it.label).toStatusTypeEntity() }) }
                    trySend(types)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveStatusType(type: String, label: String, isSystem: Boolean): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(type).set(StatusTypeDocument(type = type, label = label, isSystem = isSystem)).await()
        }

    override suspend fun deleteStatusType(type: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(type).delete().await()
        }

    override suspend fun countByStatusType(statusType: String): Result<Int, DataError.Firestore> =
        firestoreSafeCall {
            val firestoreCount = firestore.collection("employee_status")
                .whereEqualTo("status", statusType)
                .get().await()
                .size()
            val localUnsyncedCount = employeeStatusDao.countUnsyncedByStatusType(statusType)
            firestoreCount + localUnsyncedCount
        }

    override suspend fun replaceAndDeleteStatusType(oldType: String, newType: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            val statuses = firestore.collection("employee_status")
                .whereEqualTo("status", oldType)
                .get().await()
            if (statuses.documents.isNotEmpty()) {
                val batch = firestore.batch()
                statuses.documents.forEach { batch.update(it.reference, "status", newType) }
                batch.commit().await()
            }
            employeeStatusDao.replaceStatusType(oldType, newType)
            collection.document(oldType).delete().await()
        }

    private suspend fun seedDefaults() {
        listOf(
            StatusTypeDocument(type = "Office", label = "В офісі", isSystem = true),
            StatusTypeDocument(type = "Remote", label = "Віддалено", isSystem = true),
            StatusTypeDocument(type = "Sick", label = "Лікарняний", isSystem = true)
        ).forEach { collection.document(it.type).set(it).await() }
    }
}
