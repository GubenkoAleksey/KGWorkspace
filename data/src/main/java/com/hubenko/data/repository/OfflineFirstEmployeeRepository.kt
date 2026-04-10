package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.mapper.toDomain
import com.hubenko.data.mapper.toDocument
import com.hubenko.data.mapper.toEmployeeEntity
import com.hubenko.data.remote.document.EmployeeDocument
import com.hubenko.data.util.firestoreSafeCall
import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.EmployeeRepository
import com.hubenko.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OfflineFirstEmployeeRepository @Inject constructor(
    private val dao: EmployeeDao,
    private val firestore: FirebaseFirestore
) : EmployeeRepository {

    private val employeesCollection = firestore.collection("users")

    override fun getEmployeeById(id: String): Flow<Employee?> = flow {
        emit(dao.getEmployeeById(id)?.toDomain())
    }

    override fun getAllEmployees(): Flow<List<Employee>> = channelFlow {
        launch { syncEmployeesFromFirestore() }
        dao.getAllEmployees()
            .map { list -> list.map { it.toDomain() } }
            .collect { send(it) }
    }

    private suspend fun syncEmployeesFromFirestore() {
        try {
            val snapshot = employeesCollection.get().await()
            snapshot.documents.forEach { doc ->
                val document = doc.toObject(EmployeeDocument::class.java) ?: return@forEach
                dao.insertEmployee(document.toEmployeeEntity(doc.id))
            }
        } catch (_: Exception) {
        }
    }

    override suspend fun saveEmployee(employee: Employee): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            dao.insertEmployee(employee.toDocument().toEmployeeEntity(employee.id))
            employeesCollection.document(employee.id).set(employee.toDocument()).await()
        }

    override suspend fun deleteEmployee(id: String): EmptyResult<DataError.Firestore> =
        firestoreSafeCall {
            dao.deleteEmployee(id)
            employeesCollection.document(id).delete().await()
        }
}
