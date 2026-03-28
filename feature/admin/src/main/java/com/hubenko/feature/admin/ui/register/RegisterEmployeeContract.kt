package com.hubenko.feature.admin.ui.register

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.Role

data class RegisterEmployeeState(
    val isLoading: Boolean = false,
    val roles: List<Role> = emptyList(),
    val email: String = "",
    val password: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val phone: String = "",
    val role: String = ""
) : ViewState

sealed class RegisterEmployeeIntent : ViewIntent {
    data class EmailChanged(val value: String) : RegisterEmployeeIntent()
    data class PasswordChanged(val value: String) : RegisterEmployeeIntent()
    data class LastNameChanged(val value: String) : RegisterEmployeeIntent()
    data class FirstNameChanged(val value: String) : RegisterEmployeeIntent()
    data class MiddleNameChanged(val value: String) : RegisterEmployeeIntent()
    data class PhoneChanged(val value: String) : RegisterEmployeeIntent()
    data class RoleChanged(val value: String) : RegisterEmployeeIntent()
    data object Submit : RegisterEmployeeIntent()
    data object NavigateBack : RegisterEmployeeIntent()
}

sealed class RegisterEmployeeEffect : ViewSideEffect {
    data object NavigateBack : RegisterEmployeeEffect()
    data class ShowToast(val message: String) : RegisterEmployeeEffect()
}

