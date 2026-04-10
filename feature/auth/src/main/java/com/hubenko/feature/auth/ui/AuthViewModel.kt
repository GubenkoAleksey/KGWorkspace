package com.hubenko.feature.auth.ui

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.toUiText
import com.hubenko.domain.repository.AuthDataSource
import com.hubenko.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthDataSource
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
            when (val result = authRepository.signIn(state.email, state.pass)) {
                is Result.Success -> {
                    updateState { copy(isLoading = false) }
                    sendEffect(AuthEffect.NavigateToHome)
                }
                is Result.Error -> {
                    val errorUiText = result.error.toUiText()
                    updateState { copy(isLoading = false, error = errorUiText) }
                    sendEffect(AuthEffect.ShowError(errorUiText))
                }
            }
        }
    }
}
