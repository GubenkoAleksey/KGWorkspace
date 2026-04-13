package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.BaseRate
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface BaseRateDataSource {
    fun getBaseRates(): Flow<List<BaseRate>>
    suspend fun saveBaseRate(id: String, label: String, value: Double): EmptyResult<DataError.Firestore>
    suspend fun deleteBaseRate(id: String): EmptyResult<DataError.Firestore>
}
