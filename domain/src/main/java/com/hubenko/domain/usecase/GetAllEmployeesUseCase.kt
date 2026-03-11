package com.hubenko.domain.usecase

import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case для отримання списку всіх співробітників компанії.
 * Повертає [Flow] зі списком [Employee], відсортованим за алфавітом.
 */
class GetAllEmployeesUseCase @Inject constructor(
    private val repository: EmployeeRepository
) {
    operator fun invoke(): Flow<List<Employee>> = repository.getAllEmployees()
}
