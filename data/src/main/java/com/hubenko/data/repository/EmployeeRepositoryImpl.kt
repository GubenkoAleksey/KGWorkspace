package com.hubenko.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val dao: EmployeeDao,
    private val firestore: FirebaseFirestore
) : EmployeeRepository {

    private val employeesCollection = firestore.collection("employees")

    override fun getEmployeeById(id: String): Flow<Employee?> {
        return kotlinx.coroutines.flow.flow {
            emit(dao.getEmployeeById(id)?.toDomain())
        }
    }

    override fun getAllEmployees(): Flow<List<Employee>> {
        // В ідеалі тут має бути логіка синхронізації, 
        // але для адмін-панелі ми можемо довіряти локальній базі, 
        // якщо вона синхронізується, або читати напряму.
        // Для спрощення зараз повертаємо локальні дані, які оновлюються через Firestore Snapshot Listener (якщо він є)
        return dao.getAllEmployees().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveEmployee(employee: Employee) {
        // Зберігаємо локально
        dao.insertEmployee(employee.toEntity())
        
        // Зберігаємо в Firestore
        try {
            employeesCollection.document(employee.id).set(employee).await()
        } catch (e: Exception) {
            // Можна додати логіку черги синхронізації, якщо офлайн
            throw e
        }
    }

    override suspend fun deleteEmployee(id: String) {
        // Видаляємо локально
        dao.deleteEmployee(id)
        
        // Видаляємо в Firestore
        try {
            employeesCollection.document(id).delete().await()
        } catch (e: Exception) {
            throw e
        }
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
