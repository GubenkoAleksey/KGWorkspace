package com.hubenko.domain.usecase

import com.hubenko.domain.repository.AuthRepository
import javax.inject.Inject

class CheckAdminStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        val uid = authRepository.getCurrentUserId() ?: return false
        val role = authRepository.getUserRole(uid)
        return role.uppercase() == "ADMIN"
    }
}
