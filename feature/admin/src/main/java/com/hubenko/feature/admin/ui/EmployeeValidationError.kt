package com.hubenko.feature.admin.ui

import com.hubenko.core.presentation.UiText
import com.hubenko.domain.util.Error
import com.hubenko.feature.admin.R

enum class EmployeeValidationError : Error {
    EMPTY_NAME,
    EMPTY_EMAIL,
    INVALID_EMAIL,
    EMPTY_PHONE
}

fun EmployeeValidationError.toUiText(): UiText = when (this) {
    EmployeeValidationError.EMPTY_NAME -> UiText.StringResource(R.string.error_employee_empty_name)
    EmployeeValidationError.EMPTY_EMAIL -> UiText.StringResource(R.string.error_employee_empty_email)
    EmployeeValidationError.INVALID_EMAIL -> UiText.StringResource(R.string.error_employee_invalid_email)
    EmployeeValidationError.EMPTY_PHONE -> UiText.StringResource(R.string.error_employee_empty_phone)
}
