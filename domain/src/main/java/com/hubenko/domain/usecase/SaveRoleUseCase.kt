package com.hubenko.domain.usecase

import com.hubenko.domain.error.DataError
import com.hubenko.domain.repository.RoleDataSource
import com.hubenko.domain.util.EmptyResult
import javax.inject.Inject

class SaveRoleUseCase @Inject constructor(
    private val repository: RoleDataSource
) {
    suspend operator fun invoke(id: String, label: String): EmptyResult<DataError.Firestore> =
        repository.saveRole(id, label)
}
