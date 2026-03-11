package com.hubenko.domain.usecase

import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.EmployeeRepository
import javax.inject.Inject

/**
 * Use case для збереження даних співробітника.
 * Використовується як для створення нового запису, так і для оновлення існуючого.
 */
class SaveEmployeeUseCase @Inject constructor(
    private val repository: EmployeeRepository
) {
    /**
     * Зберігає [employee] у репозиторії.
     */
    suspend operator fun invoke(employee: Employee) = repository.saveEmployee(employee)
}
