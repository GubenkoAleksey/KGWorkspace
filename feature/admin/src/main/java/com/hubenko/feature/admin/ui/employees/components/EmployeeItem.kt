package com.hubenko.feature.admin.ui.employees.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.ui.model.EmployeeUi

@Composable
fun EmployeeItem(
    employee: EmployeeUi,
    roleLabel: String? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Тел: ${employee.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (employee.email.isNotEmpty()) {
                    Text(
                        text = "Email: ${employee.email}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "Роль: ${roleLabel ?: employee.role}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.cd_edit),
                    tint = MaterialTheme.colorScheme.secondaryText()
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmployeeItemPreview() {
    CoreTheme {
        EmployeeItem(
            employee = EmployeeUi(
                id = "1",
                lastName = "Іванов",
                firstName = "Іван",
                middleName = "Іванович",
                fullName = "Іванов Іван Іванович",
                phoneNumber = "+380991234567",
                role = "USER",
                email = "ivan@company.com"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmployeeItemDarkPreview() {
    CoreTheme(darkTheme = true) {
        EmployeeItem(
            employee = EmployeeUi(
                id = "1",
                lastName = "Іванов",
                firstName = "Іван",
                middleName = "Іванович",
                fullName = "Іванов Іван Іванович",
                phoneNumber = "+380991234567",
                role = "USER",
                email = "ivan@company.com"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}
