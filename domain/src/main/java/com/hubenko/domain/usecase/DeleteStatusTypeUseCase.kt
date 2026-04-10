package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.StatusTypeDataSource
import com.hubenko.domain.util.EmptyResult
import javax.inject.Inject

class DeleteStatusTypeUseCase @Inject constructor(
    private val repository: StatusTypeDataSource
) {
    suspend operator fun invoke(type: String): EmptyResult<DataError.Firestore> =
        repository.deleteStatusType(type)
}
