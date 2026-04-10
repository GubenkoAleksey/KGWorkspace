package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.StatusType
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface StatusTypeDataSource {
    fun getStatusTypes(): Flow<List<StatusType>>
    suspend fun saveStatusType(type: String, label: String): EmptyResult<DataError.Firestore>
    suspend fun deleteStatusType(type: String): EmptyResult<DataError.Firestore>
}
