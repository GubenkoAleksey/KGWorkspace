package com.hubenko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hubenko.data.local.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: String): EmployeeEntity?

    @Query("SELECT * FROM employees ORDER BY lastName ASC, firstName ASC, middleName ASC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteEmployee(id: String)
}
