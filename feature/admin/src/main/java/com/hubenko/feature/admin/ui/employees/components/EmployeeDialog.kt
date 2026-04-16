package com.hubenko.feature.admin.ui.employees.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTextField
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.model.BaseRateUi
import com.hubenko.feature.admin.ui.model.EmployeeUi
import com.hubenko.feature.admin.ui.model.HourlyRateUi
import com.hubenko.feature.admin.ui.model.RoleUi

@Composable
fun EmployeeDialog(
    employee: EmployeeUi?,
    roles: List<RoleUi>,
    baseRates: List<BaseRateUi>,
    hourlyRates: List<HourlyRateUi>,
    onDismiss: () -> Unit,
    onSave: (EmployeeUi) -> Unit
) {
    var lastName by remember { mutableStateOf(employee?.lastName ?: "") }
    var firstName by remember { mutableStateOf(employee?.firstName ?: "") }
    var middleName by remember { mutableStateOf(employee?.middleName ?: "") }
    var phoneNumber by remember { mutableStateOf(employee?.phoneNumber ?: "") }
    var role by remember { mutableStateOf(employee?.role ?: "USER") }
    var email by remember { mutableStateOf(employee?.email ?: "") }
    var baseRateId by remember { mutableStateOf(employee?.baseRateId ?: "") }
    var baseRateValueText by remember {
        mutableStateOf(
            employee?.baseRateValue?.takeIf { it != 0.0 }?.toString() ?: ""
        )
    }
    var hourlyRateId by remember { mutableStateOf(employee?.hourlyRateId ?: "") }
    var hourlyRateValueText by remember {
        mutableStateOf(
            employee?.hourlyRateValue?.takeIf { it != 0.0 }?.toString() ?: ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(if (employee == null) "Додати співробітника" else "Редагувати співробітника") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTextField(value = lastName, onValueChange = { lastName = it }, label = "Прізвище", modifier = Modifier.fillMaxWidth())
                AppTextField(value = firstName, onValueChange = { firstName = it }, label = "Ім'я", modifier = Modifier.fillMaxWidth())
                AppTextField(value = middleName, onValueChange = { middleName = it }, label = "По батькові", modifier = Modifier.fillMaxWidth())
                AppTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = "Телефон", modifier = Modifier.fillMaxWidth())
                AppTextField(value = email, onValueChange = { email = it }, label = "Електронна пошта", modifier = Modifier.fillMaxWidth())
                RoleDropdown(
                    selectedRole = role,
                    roles = roles,
                    onRoleSelected = { role = it },
                    modifier = Modifier.fillMaxWidth()
                )
                RateDropdown(
                    label = "Основна ставка",
                    unit = "грн",
                    items = baseRates.map { RateEntry(it.id, it.label, it.value) },
                    selectedId = baseRateId,
                    customValueText = baseRateValueText,
                    onCatalogSelected = { id, value ->
                        baseRateId = id
                        if (id.isNotEmpty()) baseRateValueText = value.toString()
                    },
                    onCustomValueChange = { baseRateValueText = it },
                    modifier = Modifier.fillMaxWidth()
                )
                RateDropdown(
                    label = "Погодинна ставка",
                    unit = "грн/год",
                    items = hourlyRates.map { RateEntry(it.id, it.label, it.value) },
                    selectedId = hourlyRateId,
                    customValueText = hourlyRateValueText,
                    onCatalogSelected = { id, value ->
                        hourlyRateId = id
                        if (id.isNotEmpty()) hourlyRateValueText = value.toString()
                    },
                    onCustomValueChange = { hourlyRateValueText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val fullName = listOf(lastName, firstName, middleName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                onSave(
                    EmployeeUi(
                        id = employee?.id ?: "",
                        lastName = lastName,
                        firstName = firstName,
                        middleName = middleName,
                        fullName = fullName,
                        phoneNumber = phoneNumber,
                        role = role,
                        email = email,
                        baseRateId = baseRateId,
                        baseRateValue = baseRateValueText.toDoubleOrNull() ?: 0.0,
                        hourlyRateId = hourlyRateId,
                        hourlyRateValue = hourlyRateValueText.toDoubleOrNull() ?: 0.0
                    )
                )
            }) { Text("Зберегти") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати") }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun EmployeeDialogPreview() {
    CoreTheme {
        EmployeeDialog(
            employee = EmployeeUi(
                id = "1",
                lastName = "Іванов",
                firstName = "Іван",
                middleName = "Іванович",
                fullName = "Іванов Іван Іванович",
                phoneNumber = "+380991234567",
                role = "USER",
                email = "ivan@company.com",
                baseRateId = "1",
                baseRateValue = 7100.0,
                hourlyRateId = "",
                hourlyRateValue = 95.0
            ),
            roles = listOf(RoleUi("USER", "Працівник"), RoleUi("ADMIN", "Адміністратор")),
            baseRates = listOf(
                BaseRateUi("1", "Мінімальна (7100 грн)", 7100.0),
                BaseRateUi("2", "Стандартна (12000 грн)", 12000.0)
            ),
            hourlyRates = listOf(
                HourlyRateUi("1", "Базова (85 грн/год)", 85.0),
                HourlyRateUi("2", "Підвищена (120 грн/год)", 120.0)
            ),
            onDismiss = {},
            onSave = {}
        )
    }
}
