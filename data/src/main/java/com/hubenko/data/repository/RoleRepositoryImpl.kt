package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.domain.model.Role
import com.hubenko.domain.repository.RoleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RoleRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore
) : RoleRepository {

    private val collection = firestore.collection("roles")

    override fun getRoles(): Flow<List<Role>> = callbackFlow {
        val listener = collection
            .orderBy("label")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                if (snapshot.isEmpty) {
                    CoroutineScope(Dispatchers.IO).launch { seedDefaults() }
                } else {
                    val roles = snapshot.documents.mapNotNull { doc ->
                        val id = doc.getString("id") ?: return@mapNotNull null
                        val label = doc.getString("label") ?: id
                        Role(id = id, label = label)
                    }
                    trySend(roles)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun saveRole(id: String, label: String): Result<Unit> {
        return try {
            collection.document(id).set(mapOf("id" to id, "label" to label)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRole(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedDefaults() {
        val defaults = listOf(
            "USER" to "Працівник",
            "ADMIN" to "Адміністратор"
        )
        defaults.forEach { (id, label) ->
            collection.document(id).set(mapOf("id" to id, "label" to label)).await()
        }
    }
}
