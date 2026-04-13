package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.HourlyRate
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface HourlyRateDataSource {
    fun getHourlyRates(): Flow<List<HourlyRate>>
    suspend fun saveHourlyRate(id: String, label: String, value: Double): EmptyResult<DataError.Firestore>
    suspend fun deleteHourlyRate(id: String): EmptyResult<DataError.Firestore>
}
