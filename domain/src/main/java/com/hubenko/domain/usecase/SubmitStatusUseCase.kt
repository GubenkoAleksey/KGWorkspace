package com.hubenko.domain.usecase

import com.hubenko.domain.repository.AuthRepository
import com.hubenko.domain.repository.StatusRepository
import javax.inject.Inject

class SubmitStatusUseCase @Inject constructor(
    private val statusRepository: StatusRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(status: String, note: String? = null): Result<Unit> {
        return try {
            val employeeId = authRepository.getCurrentUserId() 
                ?: return Result.failure(Exception("User not authenticated"))
            
            statusRepository.saveStatusLocally(employeeId, status, note)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
