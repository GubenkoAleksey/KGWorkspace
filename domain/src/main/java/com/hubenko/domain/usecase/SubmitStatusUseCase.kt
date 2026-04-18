package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.AuthDataSource
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.Result
import javax.inject.Inject

class SubmitStatusUseCase @Inject constructor(
    private val statusRepository: StatusRepository,
    private val authRepository: AuthDataSource
) {
    suspend operator fun invoke(
        status: String,
        note: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): EmptyResult<DataError.Local> {
        val employeeId = authRepository.getCurrentUserId()
            ?: return Result.Error(DataError.Local.UNKNOWN)
        return statusRepository.saveStatusLocally(employeeId, status, note, startTime, endTime)
    }
}
