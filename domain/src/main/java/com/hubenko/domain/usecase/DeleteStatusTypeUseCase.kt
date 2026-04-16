package com.hubenko.domain.usecase

import com.hubenko.domain.error.DirectoryError
import com.hubenko.domain.repository.StatusTypeDataSource
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.mapError
import javax.inject.Inject

class DeleteStatusTypeUseCase @Inject constructor(
    private val repository: StatusTypeDataSource
) {
    suspend operator fun invoke(type: String): EmptyResult<DirectoryError> =
        repository.deleteStatusType(type).mapError { DirectoryError.Firestore(it) }
}
