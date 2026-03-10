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
            is AuthIntent.EmailChanged -> updateState { copy(email = intent.value) }
            is AuthIntent.PasswordChanged -> updateState { copy(pass = intent.value) }
            is AuthIntent.FirstNameChanged -> updateState { copy(firstName = intent.value) }
            is AuthIntent.LastNameChanged -> updateState { copy(lastName = intent.value) }
            is AuthIntent.MiddleNameChanged -> updateState { copy(middleName = intent.value) }
            is AuthIntent.PhoneChanged -> updateState { copy(phone = intent.value) }
            is AuthIntent.AdminRoleChanged -> updateState { copy(isAdmin = intent.value) }
            is AuthIntent.ToggleAuthMode -> updateState { copy(isSignUp = !isSignUp) }
            is AuthIntent.Submit -> {
                if (viewState.value.isSignUp) signUp() else signIn()
            }
        }
    }

    private fun signIn() {
        val state = viewState.value
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val result = authRepository.signIn(state.email, state.pass)
            
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

    private fun signUp() {
        val state = viewState.value
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val role = if (state.isAdmin) "ADMIN" else "USER"
            val result = authRepository.signUp(
                state.email, state.pass, state.lastName,
                state.firstName, state.middleName, state.phone, role
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
