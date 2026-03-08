package com.hubenko.firestoreapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hubenko.firestoreapp.data.repository.StatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatusViewModel(
    private val repository: StatusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatusUiState>(StatusUiState.Idle)
    val uiState: StateFlow<StatusUiState> = _uiState.asStateFlow()

    // Using a hardcoded employee ID for demo purposes
    private val employeeId = "emp_12345"

    fun submitStatus(status: String) {
        viewModelScope.launch {
            _uiState.value = StatusUiState.Loading
            try {
                repository.saveStatusLocally(employeeId, status)
                _uiState.value = StatusUiState.Success
            } catch (e: Exception) {
                _uiState.value = StatusUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun resetState() {
        _uiState.value = StatusUiState.Idle
    }
}

sealed class StatusUiState {
    object Idle : StatusUiState()
    object Loading : StatusUiState()
    object Success : StatusUiState()
    data class Error(val message: String) : StatusUiState()
}

class StatusViewModelFactory(private val repository: StatusRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
