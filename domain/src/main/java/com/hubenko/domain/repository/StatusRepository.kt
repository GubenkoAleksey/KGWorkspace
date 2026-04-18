package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface StatusRepository {
    fun getAllStatuses(): Flow<List<EmployeeStatus>>
    suspend fun saveStatusLocally(
        employeeId: String,
        status: String,
        note: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): EmptyResult<DataError.Local>
    suspend fun getUnsyncedStatuses(): List<EmployeeStatus>
    suspend fun syncStatuses(statuses: List<EmployeeStatus>): EmptyResult<DataError.Firestore>
    fun triggerSync()
    suspend fun fetchStatusesFromRemote(): EmptyResult<DataError.Firestore>
    suspend fun deleteAllStatuses(): EmptyResult<DataError.Firestore>
    suspend fun getActiveStatus(employeeId: String): EmployeeStatus?
    suspend fun updateStatusEndTime(id: String, endTime: Long): EmptyResult<DataError.Local>
    suspend fun deleteStatus(id: String): EmptyResult<DataError.Firestore>
    suspend fun updateStatus(id: String, status: String, startTime: Long, endTime: Long?): EmptyResult<DataError.Local>
    suspend fun getStatusCountForToday(employeeId: String, startOfDay: Long): Int
    suspend fun getSickStatusForToday(employeeId: String, startOfDay: Long): EmployeeStatus?
}
