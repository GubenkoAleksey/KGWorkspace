package com.hubenko.domain.usecase

import com.hubenko.domain.repository.AuthDataSource
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthDataSource
) {
    operator fun invoke() {
        authRepository.signOut()
    }
}
