package com.hubenko.domain.usecase

import com.hubenko.domain.repository.EmployeeRepository
import javax.inject.Inject

class DeleteEmployeeUseCase @Inject constructor(
    private val repository: EmployeeRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteEmployee(id)
}
