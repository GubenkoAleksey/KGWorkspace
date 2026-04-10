package com.hubenko.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {
    fun isDarkTheme(): Flow<Boolean>
    suspend fun toggleTheme()
    suspend fun setDarkTheme(isDark: Boolean)
}
