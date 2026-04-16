package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.HourlyRateDao
import com.hubenko.data.local.entity.HourlyRateEntity
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
    private val dao: HourlyRateDao,
    firestore: FirebaseFirestore
) : HourlyRateDataSource {

    private val collection = firestore.collection("hourly_rates")

    override fun getHourlyRates(): Flow<List<HourlyRate>> = callbackFlow {
        val scope = this
        val listener = collection
            .orderBy("label")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                if (snapshot.isEmpty) {
                    scope.launch { seedDefaults() }
                } else {
                    val rates = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(HourlyRateDocument::class.java)?.toHourlyRate()
                    }
                    scope.launch { dao.insertAll(rates.map { HourlyRateEntity(id = it.id, label = it.label, value = it.value) }) }
                    trySend(rates)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveHourlyRate(id: String, label: String, value: Double, isSystem: Boolean): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).set(HourlyRateDocument(id = id, label = label, value = value, isSystem = isSystem)).await()
        }

    override suspend fun deleteHourlyRate(id: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).delete().await()
        }

    private suspend fun seedDefaults() {
        listOf(
            HourlyRateDocument(id = "RATE_50", label = "50 грн/год", value = 50.0),
            HourlyRateDocument(id = "RATE_100", label = "100 грн/год", value = 100.0),
            HourlyRateDocument(id = "RATE_150", label = "150 грн/год", value = 150.0)
        ).forEach { collection.document(it.id).set(it).await() }
    }
}
