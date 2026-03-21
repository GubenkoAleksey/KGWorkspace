package com.hubenko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hubenko.data.local.entity.EmployeeStatusEntity
import com.hubenko.data.local.entity.EmployeeStatusWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: EmployeeStatusEntity)

    @Query("SELECT * FROM employee_status WHERE isSynced = 0 ORDER BY startTime DESC")
    suspend fun getUnsyncedStatuses(): List<EmployeeStatusEntity>

    @Transaction
    @Query("SELECT * FROM employee_status ORDER BY startTime DESC")
    fun getAllStatusesWithDetails(): Flow<List<EmployeeStatusWithDetails>>

    @Query("SELECT * FROM employee_status ORDER BY startTime DESC")
    fun getAllStatuses(): Flow<List<EmployeeStatusEntity>>

    @Query("UPDATE employee_status SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>)

    @Query("DELETE FROM employee_status")
    suspend fun deleteAllStatuses()

    @Query("SELECT * FROM employee_status WHERE employeeId = :employeeId AND endTime IS NULL LIMIT 1")
    suspend fun getActiveStatus(employeeId: String): EmployeeStatusEntity?

    @Query("UPDATE employee_status SET endTime = :endTime, isSynced = 0 WHERE id = :id")
    suspend fun updateEndTime(id: String, endTime: Long)

    @Query("SELECT * FROM employee_status WHERE id = :id LIMIT 1")
    suspend fun getStatusById(id: String): EmployeeStatusEntity?
}
