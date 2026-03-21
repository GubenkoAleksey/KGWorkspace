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

    @Query("SELECT * FROM employee_status WHERE isSynced = 0 ORDER BY timestamp DESC")
    suspend fun getUnsyncedStatuses(): List<EmployeeStatusEntity>

    @Transaction
    @Query("SELECT * FROM employee_status ORDER BY timestamp DESC")
    fun getAllStatusesWithDetails(): Flow<List<EmployeeStatusWithDetails>>

    @Query("SELECT * FROM employee_status ORDER BY timestamp DESC")
    fun getAllStatuses(): Flow<List<EmployeeStatusEntity>>

    @Query("UPDATE employee_status SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>)

    @Query("DELETE FROM employee_status")
    suspend fun deleteAllStatuses()
}
