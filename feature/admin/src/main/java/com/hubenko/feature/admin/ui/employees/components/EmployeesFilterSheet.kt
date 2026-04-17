package com.hubenko.feature.admin.ui.employees.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EmployeesFilterSheet(
    currentSelectedRoles: Set<String>,
    currentSelectedEmployeeIds: Set<String>,
    roles: List<Pair<String, String>>,
    employees: List<Pair<String, String>>,
    onApply: (roles: Set<String>, employeeIds: Set<String>) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedRoles by remember { mutableStateOf(currentSelectedRoles) }
    var selectedEmployeeIds by remember { mutableStateOf(currentSelectedEmployeeIds) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredEmployees = remember(searchQuery, employees) {
        if (searchQuery.isBlank()) employees
        else employees.filter { (_, name) -> name.contains(searchQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Фільтри", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = {
                    selectedRoles = emptySet()
                    selectedEmployeeIds = emptySet()
                    searchQuery = ""
                    onClear()
                }) {
                    Text("Скинути")
                }
            }

            Text(
                text = "Працівник",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Пошук за прізвищем...") },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистити пошук")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
                    .padding(top = 4.dp)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    filteredEmployees.forEach { (id, name) ->
                        val selected = id in selectedEmployeeIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedEmployeeIds = if (selected) {
                                        selectedEmployeeIds - id
                                    } else {
                                        selectedEmployeeIds + id
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = {
                                    selectedEmployeeIds = if (selected) {
                                        selectedEmployeeIds - id
                                    } else {
                                        selectedEmployeeIds + id
                                    }
                                }
                            )
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (roles.isNotEmpty()) {
                Text(
                    text = "Роль",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    roles.forEach { (id, label) ->
                        val selected = id in selectedRoles
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedRoles = if (selected) selectedRoles - id else selectedRoles + id
                            },
                            label = { Text(label) }
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            Button(
                onClick = { onApply(selectedRoles, selectedEmployeeIds) },
                enabled = selectedRoles.isNotEmpty() || selectedEmployeeIds.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Застосувати")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Empty")
@Composable
private fun EmployeesFilterSheetEmptyPreview() {
    CoreTheme {
        EmployeesFilterSheet(
            currentSelectedRoles = emptySet(),
            currentSelectedEmployeeIds = emptySet(),
            roles = emptyList(),
            employees = emptyList(),
            onApply = { _, _ -> },
            onClear = {},
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "With data")
@Composable
private fun EmployeesFilterSheetWithDataPreview() {
    CoreTheme {
        EmployeesFilterSheet(
            currentSelectedRoles = setOf("USER"),
            currentSelectedEmployeeIds = setOf("1"),
            roles = listOf("USER" to "Працівник", "ADMIN" to "Адміністратор"),
            employees = listOf(
                "1" to "Іванов Іван Іванович",
                "2" to "Коваленко Марія Петрівна",
                "3" to "Петренко Олег Васильович",
                "4" to "Сидоренко Анна Миколаївна",
                "5" to "Мельник Тарас Іванович"
            ),
            onApply = { _, _ -> },
            onClear = {},
            onDismiss = {}
        )
    }
}
