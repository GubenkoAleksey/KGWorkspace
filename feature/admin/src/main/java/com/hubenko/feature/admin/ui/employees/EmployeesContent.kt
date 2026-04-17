package com.hubenko.feature.admin.ui.employees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTopBar
import com.hubenko.feature.admin.R
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText
import com.hubenko.feature.admin.ui.employees.components.DeleteEmployeeDialog
import com.hubenko.feature.admin.ui.employees.components.EmployeeDialog
import com.hubenko.feature.admin.ui.employees.components.EmployeeItem
import com.hubenko.feature.admin.ui.employees.components.EmployeesFilterSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesContent(
    state: EmployeesState,
    onIntent: (EmployeesIntent) -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Керування працівниками",
                actions = {
                    BadgedBox(
                        badge = {
                            if (state.filterRoles.isNotEmpty() || state.filterEmployeeIds.isNotEmpty()) Badge()
                        }
                    ) {
                        IconButton(onClick = { onIntent(EmployeesIntent.OnFilterClick) }) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = stringResource(R.string.cd_filter)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Surface(
                onClick = { onIntent(EmployeesIntent.OnAddEmployeeClick) },
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.secondaryText(),
                tonalElevation = 0.dp,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_employee))
                }
            }
        },
        snackbarHost = { snackbarHost() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.filterRoles.isNotEmpty() || state.filterEmployeeIds.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.filterRoles.forEach { roleId ->
                        val label = state.roles.firstOrNull { it.id == roleId }?.label ?: roleId
                        FilterChip(
                            selected = true,
                            onClick = { onIntent(EmployeesIntent.OnClearFilter) },
                            label = { Text(label) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cd_clear_filter),
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        )
                    }
                    state.filterEmployeeIds.forEach { empId ->
                        val name = state.employees.firstOrNull { it.id == empId }?.lastName ?: empId
                        FilterChip(
                            selected = true,
                            onClick = { onIntent(EmployeesIntent.OnClearFilter) },
                            label = { Text(name) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cd_clear_filter),
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        )
                    }
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.displayedEmployees.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (state.filterRoles.isNotEmpty() || state.filterEmployeeIds.isNotEmpty()) "Немає працівників за обраним фільтром" else "Працівники відсутні")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.displayedEmployees, key = { it.id }) { employee ->
                        val roleLabel = state.roles.firstOrNull { it.id == employee.role }?.label
                        val baseRateLabel = resolveRateLabel(employee.baseRateValue, "грн")
                        val hourlyRateLabels = employee.hourlyRates.mapNotNull { rate ->
                            if (rate.hourlyRateValue == 0.0) return@mapNotNull null
                            val statusLabel = state.statusTypes.firstOrNull { it.type == rate.statusType }?.label
                                ?: rate.statusType
                            "$statusLabel: ${"%.2f".format(rate.hourlyRateValue)} грн/год"
                        }
                        EmployeeItem(
                            employee = employee,
                            roleLabel = roleLabel,
                            baseRateLabel = baseRateLabel,
                            hourlyRateLabels = hourlyRateLabels,
                            onEdit = { onIntent(EmployeesIntent.OnEditEmployeeClick(employee)) },
                            onDelete = { onIntent(EmployeesIntent.OnDeleteEmployeeClick(employee)) },
                            onReminderClick = { onIntent(EmployeesIntent.OnReminderClick(employee.id)) },
                            onViewStatuses = { onIntent(EmployeesIntent.OnViewStatusesClick(employee.id)) }
                        )
                    }
                }
            }
        }
    }

    if (state.isEmployeeDialogOpen) {
        EmployeeDialog(
            employee = state.editingEmployee,
            roles = state.roles,
            baseRates = state.baseRates,
            hourlyRates = state.hourlyRates,
            statusTypes = state.statusTypes,
            onDismiss = { onIntent(EmployeesIntent.OnDismissDialog) },
            onSave = { employee -> onIntent(EmployeesIntent.OnSaveEmployee(employee)) }
        )
    }

    state.employeePendingDelete?.let { employee ->
        DeleteEmployeeDialog(
            employee = employee,
            onDismiss = { onIntent(EmployeesIntent.OnDismissDeleteDialog) },
            onConfirm = { onIntent(EmployeesIntent.OnConfirmDeleteEmployee) }
        )
    }

    if (state.isFilterSheetOpen) {
        EmployeesFilterSheet(
            currentSelectedRoles = state.filterRoles,
            currentSelectedEmployeeIds = state.filterEmployeeIds,
            roles = state.roles.map { it.id to it.label },
            employees = state.employees.map { it.id to it.fullName },
            onApply = { roles, ids -> onIntent(EmployeesIntent.OnApplyFilter(roles, ids)) },
            onClear = { onIntent(EmployeesIntent.OnClearFilter) },
            onDismiss = { onIntent(EmployeesIntent.OnDismissFilterSheet) }
        )
    }
}

private fun resolveRateLabel(value: Double, unit: String): String? {
    if (value == 0.0) return null
    return "%.2f %s".format(value, unit)
}

@Preview(showBackground = true, name = "Content State")
@Composable
private fun EmployeesContentPreview() {
    CoreTheme {
        EmployeesContent(
            state = EmployeesState(
                employees = listOf(
                    com.hubenko.feature.admin.ui.model.EmployeeUi("1", "Іванов", "Іван", "Іванович", "Іванов Іван Іванович", "+380991234567", "USER", "ivan@company.com"),
                    com.hubenko.feature.admin.ui.model.EmployeeUi("2", "Петренко", "Петро", "Петрович", "Петренко Петро Петрович", "+380997654321", "ADMIN", "petro@company.com")
                ),
                displayedEmployees = listOf(
                    com.hubenko.feature.admin.ui.model.EmployeeUi("1", "Іванов", "Іван", "Іванович", "Іванов Іван Іванович", "+380991234567", "USER", "ivan@company.com"),
                    com.hubenko.feature.admin.ui.model.EmployeeUi("2", "Петренко", "Петро", "Петрович", "Петренко Петро Петрович", "+380997654321", "ADMIN", "petro@company.com")
                )
            ),
            onIntent = {},
            snackbarHost = { SnackbarHost(hostState = androidx.compose.material3.SnackbarHostState()) }
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
            snackbarHost = { SnackbarHost(hostState = androidx.compose.material3.SnackbarHostState()) }
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
            snackbarHost = { SnackbarHost(hostState = androidx.compose.material3.SnackbarHostState()) }
        )
    }
}
