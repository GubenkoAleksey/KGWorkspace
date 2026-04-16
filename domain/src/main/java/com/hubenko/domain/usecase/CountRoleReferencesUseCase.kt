package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.RoleDataSource
import com.hubenko.domain.util.Result
import javax.inject.Inject

class CountRoleReferencesUseCase @Inject constructor(
    private val dataSource: RoleDataSource
) {
    suspend operator fun invoke(roleId: String): Result<Int, DataError.Firestore> =
        dataSource.countByRole(roleId)
}
