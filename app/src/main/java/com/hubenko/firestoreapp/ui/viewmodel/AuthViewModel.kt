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

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    val role = repository.getUserRole(user.uid)
                    _userRole.value = role
                    _authState.value = AuthState.Authenticated(user, role)
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        lastName: String,
        firstName: String,
        middleName: String,
        phoneNumber: String,
        role: String = "USER"
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUp(email, password, lastName, firstName, middleName, phoneNumber, role)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    _userRole.value = role
                    _authState.value = AuthState.Authenticated(user, role)
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun fetchUserRole(uid: String) {
        viewModelScope.launch {
            val role = repository.getUserRole(uid)
            _userRole.value = role
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Idle
        _userRole.value = null
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser?, val role: String) : AuthState()
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
