package com.hubenko.domain.usecase

import com.hubenko.domain.error.DirectoryError
import com.hubenko.domain.repository.RoleDataSource
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.mapError
import javax.inject.Inject

class ReplaceAndDeleteRoleUseCase @Inject constructor(
    private val dataSource: RoleDataSource
) {
    suspend operator fun invoke(oldId: String, newId: String): EmptyResult<DirectoryError> =
        dataSource.replaceAndDeleteRole(oldId, newId).mapError { DirectoryError.Firestore(it) }
}
