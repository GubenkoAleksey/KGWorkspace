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

    private val rolesCollection = firestore.collection("roles")

    /**
     * Слухає колекцію `roles` у Firestore в реальному часі.
     * Якщо колекція порожня — автоматично засіює дефолтні ролі,
     * після чого listener отримає оновлення і відправить список.
     */
    override fun getRoles(): Flow<List<Role>> = callbackFlow {
        val listener = rolesCollection
            .orderBy("label")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                if (snapshot.isEmpty) {
                    // Колекція порожня — засіюємо дефолтні ролі
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDefaultRoles()
                    }
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

    private suspend fun seedDefaultRoles() {
        val defaults = listOf(
            hashMapOf("id" to "USER", "label" to "Працівник"),
            hashMapOf("id" to "ADMIN", "label" to "Адміністратор")
        )
        defaults.forEach { role ->
            rolesCollection.add(role).await()
        }
    }
}

