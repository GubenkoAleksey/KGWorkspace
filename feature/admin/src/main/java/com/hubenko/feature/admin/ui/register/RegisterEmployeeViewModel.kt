package com.hubenko.feature.admin.ui.register

import androidx.lifecycle.viewModelScope
import com.hubenko.core.base.BaseViewModel
import com.hubenko.domain.model.Employee
import com.hubenko.domain.usecase.GetRolesUseCase
import com.hubenko.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterEmployeeViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val getRolesUseCase: GetRolesUseCase
) : BaseViewModel<RegisterEmployeeState, RegisterEmployeeIntent, RegisterEmployeeEffect>(
    RegisterEmployeeState()
) {

    init {
        loadRoles()
    }

    override fun onIntent(intent: RegisterEmployeeIntent) {
        when (intent) {
            is RegisterEmployeeIntent.EmailChanged -> updateState { copy(email = intent.value) }
            is RegisterEmployeeIntent.PasswordChanged -> updateState { copy(password = intent.value) }
            is RegisterEmployeeIntent.LastNameChanged -> updateState { copy(lastName = intent.value) }
            is RegisterEmployeeIntent.FirstNameChanged -> updateState { copy(firstName = intent.value) }
            is RegisterEmployeeIntent.MiddleNameChanged -> updateState { copy(middleName = intent.value) }
            is RegisterEmployeeIntent.PhoneChanged -> updateState { copy(phone = intent.value) }
            is RegisterEmployeeIntent.RoleChanged -> updateState { copy(role = intent.value) }
            is RegisterEmployeeIntent.Submit -> registerEmployee()
            is RegisterEmployeeIntent.NavigateBack -> sendEffect(RegisterEmployeeEffect.NavigateBack)
        }
    }

    private fun loadRoles() {
        viewModelScope.launch {
            getRolesUseCase().collectLatest { roles ->
                updateState {
                    copy(
                        roles = roles,
                        // Автоматично вибираємо першу роль зі списку якщо ще не вибрана
                        role = if (role.isBlank() && roles.isNotEmpty()) roles.first().id else role
                    )
                }
            }
        }
    }

    private fun registerEmployee() {
        val state = viewState.value
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val employee = Employee(
                id = "",
                email = state.email,
                lastName = state.lastName,
                firstName = state.firstName,
                middleName = state.middleName,
                phoneNumber = state.phone,
                role = state.role
            )
            signUpUseCase(employee, state.password)
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    sendEffect(RegisterEmployeeEffect.ShowToast("Співробітника зареєстровано успішно"))
                    sendEffect(RegisterEmployeeEffect.NavigateBack)
                }
                .onFailure { e ->
                    updateState { copy(isLoading = false) }
                    sendEffect(RegisterEmployeeEffect.ShowToast("Помилка реєстрації: ${e.message}"))
                }
        }
    }
}

