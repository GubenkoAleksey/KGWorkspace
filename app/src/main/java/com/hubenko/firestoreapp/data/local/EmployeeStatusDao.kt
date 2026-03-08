package com.hubenko.firestoreapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: EmployeeStatusEntity)

    @Query("SELECT * FROM employee_status WHERE isSynced = 0 ORDER BY timestamp DESC")
    suspend fun getUnsyncedStatuses(): List<EmployeeStatusEntity>

    @Query("SELECT * FROM employee_status ORDER BY timestamp DESC")
    fun getAllStatuses(): Flow<List<EmployeeStatusEntity>>

    @Query("UPDATE employee_status SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>)
    
    @Query("UPDATE employee_status SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
