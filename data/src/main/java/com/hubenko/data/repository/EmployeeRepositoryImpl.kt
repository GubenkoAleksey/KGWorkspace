package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.mapper.toDomain
import com.hubenko.data.mapper.toEntity
import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val dao: EmployeeDao,
    firestore: FirebaseFirestore
) : EmployeeRepository {

    private val employeesCollection = firestore.collection("employees")

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
        
        try {
            employeesCollection.document(employee.id).set(employee).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteEmployee(id: String) {
        dao.deleteEmployee(id)
        
        try {
            employeesCollection.document(id).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }
}
