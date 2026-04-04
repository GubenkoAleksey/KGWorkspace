package com.hubenko.domain.usecase

import com.hubenko.domain.repository.StatusTypeRepository
import javax.inject.Inject

class SaveStatusTypeUseCase @Inject constructor(
    private val repository: StatusTypeRepository
) {
    suspend operator fun invoke(type: String, label: String): Result<Unit> =
        repository.saveStatusType(type, label)
}
