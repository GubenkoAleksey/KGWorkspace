package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.StatusTypeDao
import com.hubenko.data.local.entity.StatusTypeEntity
import com.hubenko.domain.model.StatusType
import com.hubenko.domain.repository.StatusTypeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StatusTypeRepositoryImpl @Inject constructor(
    private val dao: StatusTypeDao,
    firestore: FirebaseFirestore
) : StatusTypeRepository {

    private val collection = firestore.collection("status_types")

    override fun getStatusTypes(): Flow<List<StatusType>> = callbackFlow {
        val listener = collection
            .orderBy("label")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                if (snapshot.isEmpty) {
                    CoroutineScope(Dispatchers.IO).launch { seedDefaults() }
                } else {
                    val types = snapshot.documents.mapNotNull { doc ->
                        val type = doc.getString("type") ?: return@mapNotNull null
                        val label = doc.getString("label") ?: type
                        StatusType(type = type, label = label)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.insertAll(types.map { StatusTypeEntity(type = it.type, label = it.label) })
                    }
                    trySend(types)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun saveStatusType(type: String, label: String): Result<Unit> {
        return try {
            collection.document(type).set(mapOf("type" to type, "label" to label)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteStatusType(type: String): Result<Unit> {
        return try {
            collection.document(type).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedDefaults() {
        val defaults = listOf(
            "Office" to "В офісі",
            "Remote" to "Віддалено",
            "Sick" to "Лікарняний"
        )
        defaults.forEach { (type, label) ->
            collection.document(type).set(mapOf("type" to type, "label" to label)).await()
        }
    }
}
