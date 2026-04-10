package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.AuthDataSource
import com.hubenko.domain.util.Result
import com.hubenko.domain.util.map
import javax.inject.Inject

class CheckAdminStatusUseCase @Inject constructor(
    private val authRepository: AuthDataSource
) {
    suspend operator fun invoke(): Result<Boolean, DataError.Firestore> {
        val uid = authRepository.getCurrentUserId() ?: return Result.Success(false)
        return authRepository.getUserRole(uid).map { role ->
            role.uppercase() == "ADMIN"
        }
    }
}
