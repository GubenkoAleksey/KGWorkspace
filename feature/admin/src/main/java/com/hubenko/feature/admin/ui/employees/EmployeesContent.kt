package com.hubenko.feature.admin.ui.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.domain.model.Employee
import com.hubenko.feature.admin.ui.employees.components.EmployeeDialog
import com.hubenko.feature.admin.ui.employees.components.EmployeeItem

/**
 * Stateless Composable для екрана списку співробітників.
 *
 * @param state Поточний стан екрана.
 * @param onIntent Лямбда для надсилання інтентів у ViewModel.
 * @param onBackClick Callback для повернення на Dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesContent(
    state: EmployeesState,
    onIntent: (EmployeesIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Керування працівниками",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onIntent(EmployeesIntent.OnAddEmployeeClick) }) {
                Icon(Icons.Default.Add, contentDescription = "Зареєструвати нового співробітника")
            }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.employees) { employee ->
                    val roleLabel = state.roles.firstOrNull { it.id == employee.role }?.label
                    EmployeeItem(
                        employee = employee,
                        roleLabel = roleLabel,
                        onEdit = { onIntent(EmployeesIntent.OnEditEmployeeClick(employee)) },
                        onDelete = { onIntent(EmployeesIntent.OnDeleteEmployeeClick(employee.id)) }
                    )
                }
            }
        }
    }

    if (state.isEmployeeDialogOpen) {
        EmployeeDialog(
            employee = state.editingEmployee,
            roles = state.roles,
            onDismiss = { onIntent(EmployeesIntent.OnDismissDialog) },
            onSave = { employee -> onIntent(EmployeesIntent.OnSaveEmployee(employee)) }
        )
    }
}

@Preview(showBackground = true, name = "Content State")
@Composable
private fun EmployeesContentPreview() {
    CoreTheme {
        EmployeesContent(
            state = EmployeesState(
                employees = listOf(
                    Employee("1", "Іванов", "Іван", "Іванович", "+380991234567", "USER", "ivan@company.com"),
                    Employee("2", "Петренко", "Петро", "Петрович", "+380997654321", "ADMIN", "petro@company.com")
                )
            ),
            onIntent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun EmployeesContentLoadingPreview() {
    CoreTheme {
        EmployeesContent(
            state = EmployeesState(isLoading = true),
            onIntent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun EmployeesContentEmptyPreview() {
    CoreTheme {
        EmployeesContent(
            state = EmployeesState(),
            onIntent = {},
            onBackClick = {}
        )
    }
}

