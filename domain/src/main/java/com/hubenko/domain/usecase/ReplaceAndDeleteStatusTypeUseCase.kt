package com.hubenko.domain.usecase

import com.hubenko.domain.error.DirectoryError
import com.hubenko.domain.repository.StatusTypeDataSource
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.mapError
import javax.inject.Inject

class ReplaceAndDeleteStatusTypeUseCase @Inject constructor(
    private val dataSource: StatusTypeDataSource
) {
    suspend operator fun invoke(oldType: String, newType: String): EmptyResult<DirectoryError> =
        dataSource.replaceAndDeleteStatusType(oldType, newType).mapError { DirectoryError.Firestore(it) }
}
