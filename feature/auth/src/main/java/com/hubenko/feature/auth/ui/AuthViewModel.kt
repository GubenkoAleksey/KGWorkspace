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
            is AuthIntent.Submit -> signIn()
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
}
