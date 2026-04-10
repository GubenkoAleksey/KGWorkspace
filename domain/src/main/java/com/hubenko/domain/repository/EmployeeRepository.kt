package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.Employee
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    fun getEmployeeById(id: String): Flow<Employee?>
    fun getAllEmployees(): Flow<List<Employee>>
    suspend fun saveEmployee(employee: Employee): EmptyResult<DataError.Firestore>
    suspend fun deleteEmployee(id: String): EmptyResult<DataError.Firestore>
}
