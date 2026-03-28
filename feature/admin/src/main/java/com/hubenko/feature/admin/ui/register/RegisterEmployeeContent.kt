package com.hubenko.feature.admin.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.components.PrimaryActionButton
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.feature.admin.ui.register.components.RegisterEmployeeForm

/**
 * Stateless Composable для екрана реєстрації нового співробітника.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEmployeeContent(
    state: RegisterEmployeeState,
    onIntent: (RegisterEmployeeIntent) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Реєстрація співробітника",
                onBackClick = { onIntent(RegisterEmployeeIntent.NavigateBack) }
            )
        }
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
                    onEmailChange = { onIntent(RegisterEmployeeIntent.EmailChanged(it)) },
                    onPasswordChange = { onIntent(RegisterEmployeeIntent.PasswordChanged(it)) },
                    onLastNameChange = { onIntent(RegisterEmployeeIntent.LastNameChanged(it)) },
                    onFirstNameChange = { onIntent(RegisterEmployeeIntent.FirstNameChanged(it)) },
                    onMiddleNameChange = { onIntent(RegisterEmployeeIntent.MiddleNameChanged(it)) },
                    onPhoneChange = { onIntent(RegisterEmployeeIntent.PhoneChanged(it)) },
                    onRoleChange = { onIntent(RegisterEmployeeIntent.RoleChanged(it)) }
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
        RegisterEmployeeContent(state = RegisterEmployeeState(), onIntent = {})
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
        RegisterEmployeeContent(state = RegisterEmployeeState(isLoading = true), onIntent = {})
    }
}

