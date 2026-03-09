package com.hubenko.feature.auth.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthState, AuthIntent, AuthEffect>(AuthState()) {

    override fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignIn -> signIn(intent.email, intent.pass)
            is AuthIntent.SignUp -> signUp(
                intent.email, intent.pass, intent.lastName,
                intent.firstName, intent.middleName, intent.phone, intent.isAdmin
            )
        }
    }

    private fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val result = authRepository.signIn(email, pass)
            
            if (result.isSuccess) {
                updateState { copy(isLoading = false) }
                sendEffect(AuthEffect.NavigateToHome)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Login failed"
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(AuthEffect.ShowError(errorMsg))
            }
        }
    }

    private fun signUp(
        email: String, pass: String, lastName: String,
        firstName: String, middleName: String, phone: String, isAdmin: Boolean
    ) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val role = if (isAdmin) "ADMIN" else "USER"
            val result = authRepository.signUp(
                email, pass, lastName, firstName, middleName, phone, role
            )
            
            if (result.isSuccess) {
                updateState { copy(isLoading = false) }
                sendEffect(AuthEffect.NavigateToHome)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Registration failed"
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(AuthEffect.ShowError(errorMsg))
            }
        }
    }
}
