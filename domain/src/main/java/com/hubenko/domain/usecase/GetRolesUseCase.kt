package com.hubenko.domain.usecase

import com.hubenko.domain.model.Role
import com.hubenko.domain.repository.RoleDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRolesUseCase @Inject constructor(
    private val repository: RoleDataSource
) {
    operator fun invoke(): Flow<List<Role>> = repository.getRoles()
}

