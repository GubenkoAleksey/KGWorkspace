package com.hubenko.feature.admin.ui.employees.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.model.RoleUi

/**
 * Випадаючий список для вибору ролі.
 * Відображає [RoleUi.label], передає [RoleUi.id] при виборі.
 *
 * @param selectedRole Поточне значення ролі (id, напр. "USER").
 * @param roles Список ролей, завантажених з Firestore.
 * @param onRoleSelected Callback, що передає id вибраної ролі.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    selectedRole: String,
    roles: List<RoleUi>,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = roles.find { it.id == selectedRole }?.label ?: selectedRole

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = displayLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Роль") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.label) },
                    onClick = {
                        onRoleSelected(role.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "User Role")
@Composable
private fun RoleDropdownUserPreview() {
    CoreTheme {
        var role by remember { mutableStateOf("USER") }
        RoleDropdown(
            selectedRole = role,
            roles = listOf(RoleUi("USER", "Працівник"), RoleUi("ADMIN", "Адміністратор")),
            onRoleSelected = { role = it }
        )
    }
}

@Preview(showBackground = true, name = "Empty Roles")
@Composable
private fun RoleDropdownEmptyPreview() {
    CoreTheme {
        RoleDropdown(
            selectedRole = "",
            roles = emptyList(),
            onRoleSelected = {}
        )
    }
}
