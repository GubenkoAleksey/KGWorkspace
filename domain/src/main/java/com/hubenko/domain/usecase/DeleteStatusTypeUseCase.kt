package com.hubenko.domain.usecase

import com.hubenko.domain.repository.StatusTypeRepository
import javax.inject.Inject

class DeleteStatusTypeUseCase @Inject constructor(
    private val repository: StatusTypeRepository
) {
    suspend operator fun invoke(type: String): Result<Unit> =
        repository.deleteStatusType(type)
}
