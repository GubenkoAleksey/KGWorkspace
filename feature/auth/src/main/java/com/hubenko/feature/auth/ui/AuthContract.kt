package com.hubenko.feature.auth.ui

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignUp: Boolean = false,
    val email: String = "",
    val pass: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val phone: String = "",
    val isAdmin: Boolean = false
) : ViewState

sealed class AuthIntent : ViewIntent {
    object ToggleAuthMode : AuthIntent()
    data class EmailChanged(val value: String) : AuthIntent()
    data class PasswordChanged(val value: String) : AuthIntent()
    data class LastNameChanged(val value: String) : AuthIntent()
    data class FirstNameChanged(val value: String) : AuthIntent()
    data class MiddleNameChanged(val value: String) : AuthIntent()
    data class PhoneChanged(val value: String) : AuthIntent()
    data class AdminRoleChanged(val value: Boolean) : AuthIntent()
    object Submit : AuthIntent()
}

sealed class AuthEffect : ViewSideEffect {
    object NavigateToHome : AuthEffect()
    data class ShowError(val message: String) : AuthEffect()
}
