package com.hubenko.domain.usecase

import com.hubenko.domain.repository.RoleRepository
import javax.inject.Inject

class SaveRoleUseCase @Inject constructor(
    private val repository: RoleRepository
) {
    suspend operator fun invoke(id: String, label: String): Result<Unit> =
        repository.saveRole(id, label)
}
