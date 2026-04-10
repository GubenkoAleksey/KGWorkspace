package com.hubenko.domain.usecase

import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.AuthDataSource
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthDataSource
) {
    suspend operator fun invoke(employee: Employee, password: String) = repository.signUp(
        email = employee.email,
        password = password,
        lastName = employee.lastName,
        firstName = employee.firstName,
        middleName = employee.middleName,
        phoneNumber = employee.phoneNumber,
        role = employee.role
    )
}
