package com.hubenko.domain.repository

import com.hubenko.domain.model.Role
import kotlinx.coroutines.flow.Flow

interface RoleRepository {
    fun getRoles(): Flow<List<Role>>
    suspend fun saveRole(id: String, label: String): Result<Unit>
    suspend fun deleteRole(id: String): Result<Unit>
}

