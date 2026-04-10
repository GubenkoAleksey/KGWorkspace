package com.hubenko.feature.auth.ui

import com.hubenko.core.presentation.UiText
import com.hubenko.domain.util.Error
import com.hubenko.feature.auth.R

enum class AuthValidationError : Error {
    EMPTY_EMAIL,
    INVALID_EMAIL,
    EMPTY_PASSWORD,
    PASSWORD_TOO_SHORT
}

fun AuthValidationError.toUiText(): UiText = when (this) {
    AuthValidationError.EMPTY_EMAIL -> UiText.StringResource(R.string.error_auth_empty_email)
    AuthValidationError.INVALID_EMAIL -> UiText.StringResource(R.string.error_auth_invalid_email)
    AuthValidationError.EMPTY_PASSWORD -> UiText.StringResource(R.string.error_auth_empty_password)
    AuthValidationError.PASSWORD_TOO_SHORT -> UiText.StringResource(R.string.error_auth_password_too_short)
}
