package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.BaseRateDataSource
import com.hubenko.domain.util.EmptyResult
import javax.inject.Inject

class SaveBaseRateUseCase @Inject constructor(
    private val repository: BaseRateDataSource
) {
    suspend operator fun invoke(id: String, label: String, value: Double): EmptyResult<DataError.Firestore> =
        repository.saveBaseRate(id, label, value)
}
