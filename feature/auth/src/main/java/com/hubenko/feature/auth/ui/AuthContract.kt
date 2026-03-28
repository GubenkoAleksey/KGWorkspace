package com.hubenko.feature.auth.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val pass: String = ""
) : ViewState

sealed class AuthIntent : ViewIntent {
    data class EmailChanged(val value: String) : AuthIntent()
    data class PasswordChanged(val value: String) : AuthIntent()
    object Submit : AuthIntent()
}

sealed class AuthEffect : ViewSideEffect {
    object NavigateToHome : AuthEffect()
    data class ShowError(val message: String) : AuthEffect()
}
