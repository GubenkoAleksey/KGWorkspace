package com.hubenko.data.repository

import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val dao: EmployeeDao
) : EmployeeRepository {

    override fun getEmployeeById(id: String): Flow<Employee?> {
        return kotlinx.coroutines.flow.flow {
            emit(dao.getEmployeeById(id)?.toDomain())
        }
    }

    override fun getAllEmployees(): Flow<List<Employee>> {
        return dao.getAllEmployees().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveEmployee(employee: Employee) {
        dao.insertEmployee(employee.toEntity())
    }

    override suspend fun deleteEmployee(id: String) {
        dao.deleteEmployee(id)
    }

    private fun EmployeeEntity.toDomain() = Employee(
        id = id,
        lastName = lastName,
        firstName = firstName,
        middleName = middleName,
        phoneNumber = phoneNumber,
        role = role
    )

    private fun Employee.toEntity() = EmployeeEntity(
        id = id,
        lastName = lastName,
        firstName = firstName,
        middleName = middleName,
        phoneNumber = phoneNumber,
        role = role
    )
}
