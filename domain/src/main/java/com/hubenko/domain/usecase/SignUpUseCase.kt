package com.hubenko.domain.usecase

import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(employee: Employee) = repository.signUp(
        email = employee.email,
        password = employee.password,
        lastName = employee.lastName,
        firstName = employee.firstName,
        middleName = employee.middleName,
        phoneNumber = employee.phoneNumber,
        role = employee.role
    )
}
