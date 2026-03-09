package com.hubenko.domain.repository

import com.hubenko.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    fun getEmployeeById(id: String): Flow<Employee?>
    fun getAllEmployees(): Flow<List<Employee>>
    suspend fun saveEmployee(employee: Employee)
    suspend fun deleteEmployee(id: String)
}
