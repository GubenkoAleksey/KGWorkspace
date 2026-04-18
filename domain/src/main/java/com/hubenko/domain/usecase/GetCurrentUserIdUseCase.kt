package com.hubenko.domain.usecase

import com.hubenko.domain.repository.AuthDataSource
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    operator fun invoke(): String? = authDataSource.getCurrentUserId()
}
