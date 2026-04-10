package com.hubenko.firestoreapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hubenko.domain.repository.SettingsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsDataSource
) : ViewModel() {

    val isLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null

    val isDarkTheme: StateFlow<Boolean?> = settingsRepository.isDarkTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun toggleTheme() {
        viewModelScope.launch {
            settingsRepository.toggleTheme()
        }
    }
}
