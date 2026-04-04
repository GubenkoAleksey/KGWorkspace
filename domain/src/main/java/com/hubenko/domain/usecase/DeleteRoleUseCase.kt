package com.hubenko.domain.usecase

import com.hubenko.domain.repository.RoleRepository
import javax.inject.Inject

class DeleteRoleUseCase @Inject constructor(
    private val repository: RoleRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> =
        repository.deleteRole(id)
}
