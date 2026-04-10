package com.hubenko.feature.admin.ui.register

import androidx.compose.runtime.Stable
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState
import com.hubenko.feature.admin.ui.model.RoleUi

@Stable
data class RegisterEmployeeState(
    val isLoading: Boolean = false,
    val roles: List<RoleUi> = emptyList(),
    val email: String = "",
    val password: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val phone: String = "",
    val role: String = ""
) : ViewState

sealed interface RegisterEmployeeIntent : ViewIntent {
    data class EmailChanged(val value: String) : RegisterEmployeeIntent
    data class PasswordChanged(val value: String) : RegisterEmployeeIntent
    data class LastNameChanged(val value: String) : RegisterEmployeeIntent
    data class FirstNameChanged(val value: String) : RegisterEmployeeIntent
    data class MiddleNameChanged(val value: String) : RegisterEmployeeIntent
    data class PhoneChanged(val value: String) : RegisterEmployeeIntent
    data class RoleChanged(val value: String) : RegisterEmployeeIntent
    data object Submit : RegisterEmployeeIntent
    data object NavigateBack : RegisterEmployeeIntent
}

sealed interface RegisterEmployeeEffect : ViewSideEffect {
    data object NavigateBack : RegisterEmployeeEffect
    data class ShowSnackbar(val message: UiText) : RegisterEmployeeEffect
}
