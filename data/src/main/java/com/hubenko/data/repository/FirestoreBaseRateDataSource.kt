package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.mapper.toBaseRate
import com.hubenko.data.remote.document.BaseRateDocument
import com.hubenko.data.util.firestoreSafeCall
import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.BaseRate
import com.hubenko.domain.repository.BaseRateDataSource
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreBaseRateDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : BaseRateDataSource {

    private val collection = firestore.collection("base_rates")

    private val migrationDefaults = mapOf(
        "FULL_TIME" to 20000.0,
        "HALF_TIME" to 10000.0,
        "QUARTER_TIME" to 5000.0
    )

    override fun getBaseRates(): Flow<List<BaseRate>> = callbackFlow {
        val scope = this
        val listener = collection
            .orderBy("label")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                if (snapshot.isEmpty) {
                    scope.launch { seedDefaults() }
                } else {
                    val docsNeedingMigration = snapshot.documents.filter { !it.contains("value") }
                    if (docsNeedingMigration.isNotEmpty()) {
                        scope.launch {
                            docsNeedingMigration.forEach { doc ->
                                val default = migrationDefaults[doc.id] ?: 0.0
                                collection.document(doc.id).update("value", default).await()
                            }
                        }
                    }
                    val rates = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(BaseRateDocument::class.java)?.toBaseRate()
                    }
                    trySend(rates)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveBaseRate(id: String, label: String, value: Double): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).set(BaseRateDocument(id = id, label = label, value = value)).await()
        }

    override suspend fun deleteBaseRate(id: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).delete().await()
        }

    private suspend fun seedDefaults() {
        val defaults = listOf(
            Triple("FULL_TIME", "Повна ставка", 20000.0),
            Triple("HALF_TIME", "Половина ставки", 10000.0),
            Triple("QUARTER_TIME", "Чверть ставки", 5000.0)
        )
        defaults.forEach { (id, label, value) ->
            collection.document(id).set(BaseRateDocument(id = id, label = label, value = value)).await()
        }
    }
}
