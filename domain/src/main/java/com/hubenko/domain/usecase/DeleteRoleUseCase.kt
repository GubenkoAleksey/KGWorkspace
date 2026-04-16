package com.hubenko.domain.usecase

import com.hubenko.domain.error.DirectoryError
import com.hubenko.domain.repository.RoleDataSource
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.mapError
import javax.inject.Inject

class DeleteRoleUseCase @Inject constructor(
    private val repository: RoleDataSource
) {
    suspend operator fun invoke(id: String): EmptyResult<DirectoryError> =
        repository.deleteRole(id).mapError { DirectoryError.Firestore(it) }
}
