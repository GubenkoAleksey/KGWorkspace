package com.hubenko.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.data.mapper.toDomain
import com.hubenko.data.mapper.toEntity
import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val dao: EmployeeDao,
    firestore: FirebaseFirestore
) : EmployeeRepository {

    private val employeesCollection = firestore.collection("users")

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

    /**
     * Fetches all employees from Firestore and upserts them into the local Room database.
     *
     * This is a suspend function intentionally called inside [kotlinx.coroutines.flow.onStart],
     * which executes within the collector's lifecycle-aware coroutine scope. This ensures the
     * operation is properly cancelled when the collector (e.g. a ViewModel) is cleared, avoiding
     * the memory leak that an unmanaged [kotlinx.coroutines.CoroutineScope] would cause.
     */
    private suspend fun syncEmployeesFromFirestore() {
        try {
            val snapshot = employeesCollection.get().await()
            snapshot.documents.forEach { doc ->
                val id = doc.getString("uid") ?: doc.getString("id") ?: doc.id
                val entity = EmployeeEntity(
                    id = id,
                    lastName = doc.getString("lastName") ?: "",
                    firstName = doc.getString("firstName") ?: "",
                    middleName = doc.getString("middleName") ?: "",
                    phoneNumber = doc.getString("phoneNumber") ?: "",
                    role = doc.getString("role") ?: "USER",
                    email = doc.getString("email") ?: ""
                )
                dao.insertEmployee(entity)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync employees from Firestore", e)
        }
    }

    override suspend fun saveEmployee(employee: Employee) {
        dao.insertEmployee(employee.toEntity())
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
    }

    override suspend fun deleteEmployee(id: String) {
        dao.deleteEmployee(id)
        employeesCollection.document(id).delete().await()
    }

    private companion object {
        private const val TAG = "EmployeeRepositoryImpl"
    }
}
