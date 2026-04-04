package com.hubenko.domain.repository

import com.hubenko.domain.model.StatusType
import kotlinx.coroutines.flow.Flow

interface StatusTypeRepository {
    fun getStatusTypes(): Flow<List<StatusType>>
    suspend fun saveStatusType(type: String, label: String): Result<Unit>
    suspend fun deleteStatusType(type: String): Result<Unit>
}
