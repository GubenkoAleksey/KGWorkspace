package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.StatusTypeDataSource
import com.hubenko.domain.util.Result
import javax.inject.Inject

class CountStatusTypeReferencesUseCase @Inject constructor(
    private val dataSource: StatusTypeDataSource
) {
    suspend operator fun invoke(statusType: String): Result<Int, DataError.Firestore> =
        dataSource.countByStatusType(statusType)
}
