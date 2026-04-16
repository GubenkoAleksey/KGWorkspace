package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.BaseRateDao
import com.hubenko.data.local.entity.BaseRateEntity
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
    private val dao: BaseRateDao,
    firestore: FirebaseFirestore
) : BaseRateDataSource {

    private val collection = firestore.collection("base_rates")

    override fun getBaseRates(): Flow<List<BaseRate>> = callbackFlow {
        val scope = this
        val listener = collection
            .orderBy("label")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                if (snapshot.isEmpty) {
                    scope.launch { seedDefaults() }
                } else {
                    val rates = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(BaseRateDocument::class.java)?.toBaseRate()
                    }
                    scope.launch { dao.insertAll(rates.map { BaseRateEntity(id = it.id, label = it.label, value = it.value) }) }
                    trySend(rates)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveBaseRate(id: String, label: String, value: Double, isSystem: Boolean): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).set(BaseRateDocument(id = id, label = label, value = value, isSystem = isSystem)).await()
        }

    override suspend fun deleteBaseRate(id: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            collection.document(id).delete().await()
        }

    private suspend fun seedDefaults() {
        listOf(
            BaseRateDocument(id = "FULL_TIME", label = "Повна ставка", value = 20000.0),
            BaseRateDocument(id = "HALF_TIME", label = "Половина ставки", value = 10000.0),
            BaseRateDocument(id = "QUARTER_TIME", label = "Чверть ставки", value = 5000.0)
        ).forEach { collection.document(it.id).set(it).await() }
    }
}
