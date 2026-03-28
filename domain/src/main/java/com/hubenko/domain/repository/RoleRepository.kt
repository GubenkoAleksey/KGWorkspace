package com.hubenko.domain.repository

import com.hubenko.domain.model.Role
import kotlinx.coroutines.flow.Flow

interface RoleRepository {
    /**
     * Повертає список ролей з Firestore.
     * Якщо колекція порожня — автоматично засіює значення за замовчуванням.
     */
    fun getRoles(): Flow<List<Role>>
}

