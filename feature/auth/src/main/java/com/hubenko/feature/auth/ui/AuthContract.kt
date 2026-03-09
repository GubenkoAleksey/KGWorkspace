package com.hubenko.feature.auth.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null
) : ViewState

sealed class AuthIntent : ViewIntent {
    data class SignIn(val email: String, val pass: String) : AuthIntent()
    data class SignUp(
        val email: String, val pass: String,
        val lastName: String, val firstName: String, val middleName: String,
        val phone: String, val isAdmin: Boolean
    ) : AuthIntent()
}

sealed class AuthEffect : ViewSideEffect {
    object NavigateToHome : AuthEffect()
    data class ShowError(val message: String) : AuthEffect()
}
