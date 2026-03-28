package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.data.mapper.toDomain
import com.hubenko.data.mapper.toEntity
import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.EmployeeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val dao: EmployeeDao,
    firestore: FirebaseFirestore
) : EmployeeRepository {

    private val employeesCollection = firestore.collection("users")
    private val syncScope = CoroutineScope(Dispatchers.IO)

    override fun getEmployeeById(id: String): Flow<Employee?> {
        return kotlinx.coroutines.flow.flow {
            emit(dao.getEmployeeById(id)?.toDomain())
        }
    }

    override fun getAllEmployees(): Flow<List<Employee>> {
        return dao.getAllEmployees()
            .map { list -> list.map { it.toDomain() } }
            .onStart { syncEmployeesFromFirestore() }
    }

    private fun syncEmployeesFromFirestore() {
        syncScope.launch {
            try {
                val snapshot = employeesCollection.get().await()
                snapshot.documents.forEach { doc ->
                    val id = doc.getString("uid") ?: doc.getString("id") ?: doc.id
                    val lastName = doc.getString("lastName") ?: ""
                    val firstName = doc.getString("firstName") ?: ""
                    val middleName = doc.getString("middleName") ?: ""
                    val phoneNumber = doc.getString("phoneNumber") ?: ""
                    val role = doc.getString("role") ?: "USER"
                    val email = doc.getString("email") ?: ""

                    val entity = EmployeeEntity(
                        id = id,
                        lastName = lastName,
                        firstName = firstName,
                        middleName = middleName,
                        phoneNumber = phoneNumber,
                        role = role,
                        email = email
                    )
                    dao.insertEmployee(entity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun saveEmployee(employee: Employee) {
        dao.insertEmployee(employee.toEntity())
        
        try {
            val employeeMap = hashMapOf(
                "uid" to employee.id,
                "id" to employee.id,
                "email" to employee.email,
                "lastName" to employee.lastName,
                "firstName" to employee.firstName,
                "middleName" to employee.middleName,
                "phoneNumber" to employee.phoneNumber,
                "role" to employee.role
            )
            employeesCollection.document(employee.id).set(employeeMap).await()
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
