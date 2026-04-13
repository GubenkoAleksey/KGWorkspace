package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.BaseRateDataSource
import com.hubenko.domain.util.EmptyResult
import javax.inject.Inject

class DeleteBaseRateUseCase @Inject constructor(
    private val repository: BaseRateDataSource
) {
    suspend operator fun invoke(id: String): EmptyResult<DataError.Firestore> =
        repository.deleteBaseRate(id)
}
