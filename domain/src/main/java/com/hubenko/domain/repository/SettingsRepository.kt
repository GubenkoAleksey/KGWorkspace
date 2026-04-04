package com.hubenko.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun isDarkTheme(): Flow<Boolean>
    suspend fun toggleTheme()
    suspend fun setDarkTheme(isDark: Boolean)
}
