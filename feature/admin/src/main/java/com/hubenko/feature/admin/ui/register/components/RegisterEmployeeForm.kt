package com.hubenko.feature.admin.ui.register.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTextField
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.employees.components.RateDropdown
import com.hubenko.feature.admin.ui.employees.components.RateEntry
import com.hubenko.feature.admin.ui.employees.components.RoleDropdown
import com.hubenko.feature.admin.ui.model.BaseRateUi
import com.hubenko.feature.admin.ui.model.HourlyRateUi
import com.hubenko.feature.admin.ui.model.RoleUi

@Composable
fun RegisterEmployeeForm(
    email: String,
    password: String,
    lastName: String,
    firstName: String,
    middleName: String,
    phone: String,
    role: String,
    roles: List<RoleUi>,
    baseRates: List<BaseRateUi>,
    hourlyRates: List<HourlyRateUi>,
    baseRateId: String,
    baseRateCustomText: String,
    hourlyRateId: String,
    hourlyRateCustomText: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onBaseRateCatalogSelected: (id: String, value: Double) -> Unit,
    onBaseRateCustomValueChange: (String) -> Unit,
    onHourlyRateCatalogSelected: (id: String, value: Double) -> Unit,
    onHourlyRateCustomValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AppTextField(value = email, onValueChange = onEmailChange, label = "Email")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = password, onValueChange = onPasswordChange, label = "Пароль", visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = lastName, onValueChange = onLastNameChange, label = "Прізвище")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = firstName, onValueChange = onFirstNameChange, label = "Ім'я")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = middleName, onValueChange = onMiddleNameChange, label = "По батькові")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = phone, onValueChange = onPhoneChange, label = "Номер телефону")
        Spacer(modifier = Modifier.height(12.dp))
        RoleDropdown(
            selectedRole = role,
            roles = roles,
            onRoleSelected = onRoleChange
        )
        Spacer(modifier = Modifier.height(12.dp))
        RateDropdown(
            label = "Основна ставка",
            unit = "грн",
            items = baseRates.map { RateEntry(it.id, it.label, it.value) },
            selectedId = baseRateId,
            customValueText = baseRateCustomText,
            onCatalogSelected = onBaseRateCatalogSelected,
            onCustomValueChange = onBaseRateCustomValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        RateDropdown(
            label = "Погодинна ставка",
            unit = "грн/год",
            items = hourlyRates.map { RateEntry(it.id, it.label, it.value) },
            selectedId = hourlyRateId,
            customValueText = hourlyRateCustomText,
            onCatalogSelected = onHourlyRateCatalogSelected,
            onCustomValueChange = onHourlyRateCustomValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, name = "Empty Form")
@Composable
private fun RegisterEmployeeFormEmptyPreview() {
    CoreTheme {
        RegisterEmployeeForm(
            email = "", password = "", lastName = "", firstName = "",
            middleName = "", phone = "", role = "",
            roles = listOf(RoleUi("USER", "Працівник"), RoleUi("ADMIN", "Адміністратор")),
            baseRates = listOf(BaseRateUi("1", "За замовчуванням", 7100.0)),
            hourlyRates = listOf(HourlyRateUi("1", "За замовчуванням", 85.0)),
            baseRateId = "", baseRateCustomText = "",
            hourlyRateId = "", hourlyRateCustomText = "",
            onEmailChange = {}, onPasswordChange = {}, onLastNameChange = {},
            onFirstNameChange = {}, onMiddleNameChange = {}, onPhoneChange = {},
            onRoleChange = {},
            onBaseRateCatalogSelected = { _, _ -> }, onBaseRateCustomValueChange = {},
            onHourlyRateCatalogSelected = { _, _ -> }, onHourlyRateCustomValueChange = {}
        )
    }
}
