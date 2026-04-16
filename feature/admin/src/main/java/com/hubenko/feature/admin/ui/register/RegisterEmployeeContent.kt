package com.hubenko.feature.admin.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTopBar
import com.hubenko.core.presentation.components.PrimaryActionButton
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.register.components.RegisterEmployeeForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEmployeeContent(
    state: RegisterEmployeeState,
    onIntent: (RegisterEmployeeIntent) -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Реєстрація співробітника")
        },
        snackbarHost = { snackbarHost() }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RegisterEmployeeForm(
                    email = state.email,
                    password = state.password,
                    lastName = state.lastName,
                    firstName = state.firstName,
                    middleName = state.middleName,
                    phone = state.phone,
                    role = state.role,
                    roles = state.roles,
                    baseRates = state.baseRates,
                    hourlyRates = state.hourlyRates,
                    baseRateId = state.baseRateId,
                    baseRateCustomText = if (state.baseRateId.isEmpty() && state.baseRateValue != 0.0)
                        state.baseRateValue.toString() else "",
                    hourlyRateId = state.hourlyRateId,
                    hourlyRateCustomText = if (state.hourlyRateId.isEmpty() && state.hourlyRateValue != 0.0)
                        state.hourlyRateValue.toString() else "",
                    onEmailChange = { onIntent(RegisterEmployeeIntent.EmailChanged(it)) },
                    onPasswordChange = { onIntent(RegisterEmployeeIntent.PasswordChanged(it)) },
                    onLastNameChange = { onIntent(RegisterEmployeeIntent.LastNameChanged(it)) },
                    onFirstNameChange = { onIntent(RegisterEmployeeIntent.FirstNameChanged(it)) },
                    onMiddleNameChange = { onIntent(RegisterEmployeeIntent.MiddleNameChanged(it)) },
                    onPhoneChange = { onIntent(RegisterEmployeeIntent.PhoneChanged(it)) },
                    onRoleChange = { onIntent(RegisterEmployeeIntent.RoleChanged(it)) },
                    onBaseRateCatalogSelected = { id, value ->
                        onIntent(RegisterEmployeeIntent.BaseRateChanged(id, value))
                    },
                    onBaseRateCustomValueChange = {
                        onIntent(RegisterEmployeeIntent.BaseRateCustomValueChanged(it))
                    },
                    onHourlyRateCatalogSelected = { id, value ->
                        onIntent(RegisterEmployeeIntent.HourlyRateChanged(id, value))
                    },
                    onHourlyRateCustomValueChange = {
                        onIntent(RegisterEmployeeIntent.HourlyRateCustomValueChanged(it))
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryActionButton(
                    text = "Зареєструвати",
                    onClick = { onIntent(RegisterEmployeeIntent.Submit) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun RegisterEmployeeContentEmptyPreview() {
    CoreTheme {
        RegisterEmployeeContent(
            state = RegisterEmployeeState(),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Filled State")
@Composable
private fun RegisterEmployeeContentFilledPreview() {
    CoreTheme {
        RegisterEmployeeContent(
            state = RegisterEmployeeState(
                email = "ivan@company.com",
                lastName = "Іванов",
                firstName = "Іван"
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun RegisterEmployeeContentLoadingPreview() {
    CoreTheme {
        RegisterEmployeeContent(
            state = RegisterEmployeeState(isLoading = true),
            onIntent = {}
        )
    }
}
