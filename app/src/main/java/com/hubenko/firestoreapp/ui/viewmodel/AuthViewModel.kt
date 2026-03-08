package com.hubenko.firestoreapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.hubenko.firestoreapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated(result.getOrNull())
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUp(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated(result.getOrNull())
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Idle
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
