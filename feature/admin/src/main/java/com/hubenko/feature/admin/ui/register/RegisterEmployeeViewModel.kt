package com.hubenko.feature.admin.ui.register

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.toUiText
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.ui.model.toBaseRateUi
import com.hubenko.feature.admin.ui.model.toHourlyRateUi
import com.hubenko.feature.admin.ui.model.toRoleUi
import com.hubenko.domain.model.Employee
import com.hubenko.domain.usecase.GetBaseRatesUseCase
import com.hubenko.domain.usecase.GetHourlyRatesUseCase
import com.hubenko.domain.usecase.GetRolesUseCase
import com.hubenko.domain.usecase.SignUpUseCase
import com.hubenko.domain.util.onFailure
import com.hubenko.domain.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterEmployeeViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val getRolesUseCase: GetRolesUseCase,
    private val getBaseRatesUseCase: GetBaseRatesUseCase,
    private val getHourlyRatesUseCase: GetHourlyRatesUseCase
) : BaseViewModel<RegisterEmployeeState, RegisterEmployeeIntent, RegisterEmployeeEffect>(
    RegisterEmployeeState()
) {

    init {
        loadData()
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
            is RegisterEmployeeIntent.BaseRateChanged -> updateState {
                copy(baseRateId = intent.id, baseRateValue = intent.value)
            }
            is RegisterEmployeeIntent.HourlyRateChanged -> updateState {
                copy(hourlyRateId = intent.id, hourlyRateValue = intent.value)
            }
            is RegisterEmployeeIntent.BaseRateCustomValueChanged -> updateState {
                copy(baseRateId = "", baseRateValue = intent.value.toDoubleOrNull() ?: 0.0)
            }
            is RegisterEmployeeIntent.HourlyRateCustomValueChanged -> updateState {
                copy(hourlyRateId = "", hourlyRateValue = intent.value.toDoubleOrNull() ?: 0.0)
            }
            is RegisterEmployeeIntent.Submit -> registerEmployee()
            is RegisterEmployeeIntent.NavigateBack -> sendEffect(RegisterEmployeeEffect.NavigateBack)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                getRolesUseCase().collectLatest { roles ->
                    updateState {
                        copy(
                            roles = roles.map { it.toRoleUi() },
                            role = if (role.isBlank() && roles.isNotEmpty()) roles.first().id else role
                        )
                    }
                }
            }
            launch {
                getBaseRatesUseCase().collectLatest { rates ->
                    updateState { copy(baseRates = rates.map { it.toBaseRateUi() }) }
                }
            }
            launch {
                getHourlyRatesUseCase().collectLatest { rates ->
                    updateState { copy(hourlyRates = rates.map { it.toHourlyRateUi() }) }
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
                role = state.role,
                baseRateId = state.baseRateId,
                baseRateValue = state.baseRateValue
            )
            signUpUseCase(employee, state.password)
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    sendEffect(RegisterEmployeeEffect.ShowSnackbar(UiText.StringResource(R.string.success_employee_registered)))
                    sendEffect(RegisterEmployeeEffect.NavigateBack)
                }
                .onFailure { error ->
                    updateState { copy(isLoading = false) }
                    sendEffect(RegisterEmployeeEffect.ShowSnackbar(error.toUiText()))
                }
        }
    }
}
