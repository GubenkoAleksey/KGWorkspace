package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.StatusType
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface StatusTypeDataSource {
    fun getStatusTypes(): Flow<List<StatusType>>
    suspend fun saveStatusType(type: String, label: String, isSystem: Boolean): EmptyResult<DataError.Firestore>
    suspend fun deleteStatusType(type: String): EmptyResult<DataError.Firestore>
    suspend fun countByStatusType(statusType: String): Result<Int, DataError.Firestore>
    suspend fun replaceAndDeleteStatusType(oldType: String, newType: String): EmptyResult<DataError.Firestore>
}
