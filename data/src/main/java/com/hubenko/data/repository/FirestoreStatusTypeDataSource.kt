package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.StatusTypeDao
import com.hubenko.data.mapper.toStatusType
import com.hubenko.data.mapper.toStatusTypeEntity
import com.hubenko.data.remote.document.StatusTypeDocument
import com.hubenko.data.util.firestoreSafeCall
import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.StatusType
import com.hubenko.domain.repository.StatusTypeDataSource
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreStatusTypeDataSource @Inject constructor(
    private val dao: StatusTypeDao,
    firestore: FirebaseFirestore
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

    override suspend fun saveStatusType(type: String, label: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(type).set(StatusTypeDocument(type = type, label = label)).await()
        }

    override suspend fun deleteStatusType(type: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(type).delete().await()
        }

    private suspend fun seedDefaults() {
        val defaults = listOf(
            "Office" to "В офісі",
            "Remote" to "Віддалено",
            "Sick" to "Лікарняний"
        )
        defaults.forEach { (type, label) ->
            collection.document(type).set(StatusTypeDocument(type = type, label = label)).await()
        }
    }
}
