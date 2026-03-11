package com.hubenko.domain.usecase

import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.domain.repository.StatusRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllStatusesUseCase @Inject constructor(
    private val repository: StatusRepository
) {
    operator fun invoke(): Flow<List<EmployeeStatus>> = repository.getAllStatuses()
}
