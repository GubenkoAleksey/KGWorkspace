package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.HourlyRateDataSource
import com.hubenko.domain.util.EmptyResult
import javax.inject.Inject

class DeleteHourlyRateUseCase @Inject constructor(
    private val repository: HourlyRateDataSource
) {
    suspend operator fun invoke(id: String): EmptyResult<DataError.Firestore> =
        repository.deleteHourlyRate(id)
}
