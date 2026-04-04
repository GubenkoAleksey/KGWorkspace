package com.hubenko.domain.usecase

import com.hubenko.domain.model.StatusType
import com.hubenko.domain.repository.StatusTypeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStatusTypesUseCase @Inject constructor(
    private val repository: StatusTypeRepository
) {
    operator fun invoke(): Flow<List<StatusType>> = repository.getStatusTypes()
}
