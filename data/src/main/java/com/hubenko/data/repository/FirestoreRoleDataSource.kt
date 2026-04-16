package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.mapper.toRole
import com.hubenko.data.remote.document.RoleDocument
import com.hubenko.data.util.firestoreSafeCall
import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.Role
import com.hubenko.domain.repository.RoleDataSource
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRoleDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoleDataSource {

    private val collection = firestore.collection("roles")

    override fun getRoles(): Flow<List<Role>> = callbackFlow {
        val scope = this
        val listener = collection
            .orderBy("label")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                if (snapshot.isEmpty) {
                    scope.launch { seedDefaults() }
                } else {
                    val roles = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(RoleDocument::class.java)?.toRole()
                    }
                    trySend(roles)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveRole(id: String, label: String, isSystem: Boolean): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).set(RoleDocument(id = id, label = label, isSystem = isSystem)).await()
        }

    override suspend fun deleteRole(id: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).delete().await()
        }

    override suspend fun countByRole(roleId: String): Result<Int, DataError.Firestore> =
        firestoreSafeCall {
            firestore.collection("employees")
                .whereEqualTo("role", roleId)
                .get().await()
                .size()
        }

    override suspend fun replaceAndDeleteRole(oldId: String, newId: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            val employees = firestore.collection("employees")
                .whereEqualTo("role", oldId)
                .get().await()
            if (employees.documents.isNotEmpty()) {
                val batch = firestore.batch()
                employees.documents.forEach { batch.update(it.reference, "role", newId) }
                batch.commit().await()
            }
            collection.document(oldId).delete().await()
        }

    private suspend fun seedDefaults() {
        listOf(
            RoleDocument(id = "ADMIN", label = "Адміністратор", isSystem = true),
            RoleDocument(id = "USER", label = "Працівник", isSystem = false)
        ).forEach { collection.document(it.id).set(it).await() }
    }
}
