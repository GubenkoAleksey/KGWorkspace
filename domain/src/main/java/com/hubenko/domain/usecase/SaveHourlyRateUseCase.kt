package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.HourlyRateDataSource
import com.hubenko.domain.util.EmptyResult
import javax.inject.Inject

class SaveHourlyRateUseCase @Inject constructor(
    private val repository: HourlyRateDataSource
) {
    suspend operator fun invoke(id: String, label: String, value: Double, isSystem: Boolean): EmptyResult<DataError.Firestore> =
        repository.saveHourlyRate(id, label, value, isSystem)
}
