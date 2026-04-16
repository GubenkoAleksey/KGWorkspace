package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.Role
import com.hubenko.domain.util.EmptyResult
import com.hubenko.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface RoleDataSource {
    fun getRoles(): Flow<List<Role>>
    suspend fun saveRole(id: String, label: String, isSystem: Boolean): EmptyResult<DataError.Firestore>
    suspend fun deleteRole(id: String): EmptyResult<DataError.Firestore>
    suspend fun countByRole(roleId: String): Result<Int, DataError.Firestore>
    suspend fun replaceAndDeleteRole(oldId: String, newId: String): EmptyResult<DataError.Firestore>
}
