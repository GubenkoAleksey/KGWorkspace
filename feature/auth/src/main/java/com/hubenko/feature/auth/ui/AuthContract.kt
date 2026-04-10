package com.hubenko.feature.auth.ui

import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState

data class AuthState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val email: String = "",
    val pass: String = ""
) : ViewState

sealed interface AuthIntent : ViewIntent {
    data class EmailChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
    data object Submit : AuthIntent
}

sealed interface AuthEffect : ViewSideEffect {
    data object NavigateToHome : AuthEffect
    data class ShowError(val message: UiText) : AuthEffect
}
