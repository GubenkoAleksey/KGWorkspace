package com.hubenko.domain.repository

import com.hubenko.domain.model.EmployeeStatus
import kotlinx.coroutines.flow.Flow

interface StatusRepository {
    fun getAllStatuses(): Flow<List<EmployeeStatus>>
    suspend fun saveStatusLocally(employeeId: String, status: String)
    suspend fun getUnsyncedStatuses(): List<EmployeeStatus>
    suspend fun syncStatuses(statuses: List<EmployeeStatus>): Result<Unit>
}
