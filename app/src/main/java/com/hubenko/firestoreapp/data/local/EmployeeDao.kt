package com.hubenko.firestoreapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: String): EmployeeEntity?

    @Query("SELECT * FROM employees WHERE lastName = :lastName AND firstName = :firstName AND middleName = :middleName LIMIT 1")
    suspend fun getEmployeeByName(lastName: String, firstName: String, middleName: String): EmployeeEntity?

    @Query("SELECT * FROM employees ORDER BY lastName ASC, firstName ASC, middleName ASC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE role = :role")
    fun getEmployeesByRole(role: String): Flow<List<EmployeeEntity>>

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteEmployee(id: String)
}
