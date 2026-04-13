package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.mapper.toHourlyRate
import com.hubenko.data.remote.document.HourlyRateDocument
import com.hubenko.data.util.firestoreSafeCall
import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.HourlyRate
import com.hubenko.domain.repository.HourlyRateDataSource
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreHourlyRateDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : HourlyRateDataSource {

    private val collection = firestore.collection("hourly_rates")

    private val migrationDefaults = mapOf(
        "RATE_50" to 50.0,
        "RATE_100" to 100.0,
        "RATE_150" to 150.0
    )

    override fun getHourlyRates(): Flow<List<HourlyRate>> = callbackFlow {
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
                        doc.toObject(HourlyRateDocument::class.java)?.toHourlyRate()
                    }
                    trySend(rates)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveHourlyRate(id: String, label: String, value: Double): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).set(HourlyRateDocument(id = id, label = label, value = value)).await()
        }

    override suspend fun deleteHourlyRate(id: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).delete().await()
        }

    private suspend fun seedDefaults() {
        val defaults = listOf(
            Triple("RATE_50", "50 грн/год", 50.0),
            Triple("RATE_100", "100 грн/год", 100.0),
            Triple("RATE_150", "150 грн/год", 150.0)
        )
        defaults.forEach { (id, label, value) ->
            collection.document(id).set(HourlyRateDocument(id = id, label = label, value = value)).await()
        }
    }
}
