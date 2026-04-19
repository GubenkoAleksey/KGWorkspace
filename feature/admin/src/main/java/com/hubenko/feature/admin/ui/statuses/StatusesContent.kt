package com.hubenko.feature.admin.ui.statuses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.feature.admin.R
import com.hubenko.core.presentation.components.AppTopBar
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.model.EmployeeStatusUi
import com.hubenko.feature.admin.ui.statuses.components.DeleteStatusDialog
import com.hubenko.feature.admin.ui.statuses.components.DeleteStatusesDialog
import com.hubenko.feature.admin.ui.statuses.components.EditStatusDialog
import com.hubenko.feature.admin.ui.statuses.components.EmployeeStatusesItem
import com.hubenko.feature.admin.ui.statuses.components.ExportFormatDialog
import com.hubenko.feature.admin.ui.statuses.components.StatusesFilterSheet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusesContent(
    state: StatusesState,
    onIntent: (StatusesIntent) -> Unit,
    onNavigateBack: () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Статуси працівників",
                actions = {
                    if (state.showPayment && state.employeeGroups.isNotEmpty()) {
                        IconButton(onClick = { onIntent(StatusesIntent.OnDeleteAllClick) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.cd_delete_all_statuses),
                                tint = Color.Red
                            )
                        }
                        IconButton(onClick = { onIntent(StatusesIntent.OnExportClick) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.cd_export_csv)
                            )
                        }
                    }
                    BadgedBox(
                        badge = {
                            if (state.filterDateFrom != null || state.filterEmployeeIds.isNotEmpty() || state.filterStatusTypes.isNotEmpty()) {
                                Badge()
                            }
                        }
                    ) {
                        IconButton(onClick = { onIntent(StatusesIntent.OnFilterClick) }) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = stringResource(R.string.cd_filter)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { snackbarHost() }
    ) { paddingValues ->
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.filterDateFrom != null && state.filterDateTo != null) {
                val label = "${dateFormatter.format(Date(state.filterDateFrom))} — " +
                        dateFormatter.format(Date(state.filterDateTo))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    FilterChip(
                        selected = true,
                        onClick = { onIntent(StatusesIntent.OnClearFilter) },
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
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.employeeGroups.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Статуси відсутні")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.employeeGroups,
                        key = { it.employeeId }
                    ) { group ->
                        EmployeeStatusesItem(
                            group = group,
                            showPayment = state.showPayment,
                            onToggleExpand = {
                                onIntent(StatusesIntent.OnEmployeeExpandToggle(group.employeeId))
                            },
                            onEditStatus = { status -> onIntent(StatusesIntent.OnStatusEditClick(status)) },
                            onDeleteStatus = { id -> onIntent(StatusesIntent.OnStatusDeleteClick(id)) }
                        )
                    }
                }
            }
        }
    }

    if (state.isExportFormatDialogOpen) {
        ExportFormatDialog(
            onFormatSelected = { format -> onIntent(StatusesIntent.OnExportFormatSelected(format)) },
            onDismiss = { onIntent(StatusesIntent.OnDismissExportDialog) }
        )
    }

    if (state.isDeleteDialogOpen) {
        DeleteStatusesDialog(
            onConfirm = { onIntent(StatusesIntent.OnConfirmDelete) },
            onDismiss = { onIntent(StatusesIntent.OnDismissDialog) }
        )
    }

    if (state.deletingStatusId != null) {
        DeleteStatusDialog(
            onConfirm = { onIntent(StatusesIntent.OnConfirmDeleteStatus) },
            onDismiss = { onIntent(StatusesIntent.OnDismissDeleteStatus) }
        )
    }

    state.editingStatus?.let { editing ->
        EditStatusDialog(
            status = editing,
            availableStatusTypes = state.availableStatusTypes,
            onSave = { statusType, startTime, endTime ->
                onIntent(StatusesIntent.OnEditStatusSave(editing.id, statusType, startTime, endTime))
            },
            onDismiss = { onIntent(StatusesIntent.OnEditStatusDismiss) }
        )
    }

    if (state.isFilterSheetOpen) {
        val dateFiltered = remember(state.statuses, state.filterDateFrom, state.filterDateTo) {
            if (state.filterDateFrom != null && state.filterDateTo != null) {
                state.statuses.filter { it.startTime in state.filterDateFrom..state.filterDateTo }
            } else {
                state.statuses
            }
        }
        val employees = remember(dateFiltered) {
            dateFiltered
                .distinctBy { it.employeeId }
                .map { it.employeeId to (it.employeeFullName?.takeIf(String::isNotBlank) ?: it.employeeId) }
        }
        val statusTypes = remember(dateFiltered, state.filterEmployeeIds, state.availableStatusTypes) {
            val employeeFiltered = if (state.filterEmployeeIds.isNotEmpty()) {
                dateFiltered.filter { it.employeeId in state.filterEmployeeIds }
            } else {
                dateFiltered
            }
            val presentTypes = employeeFiltered.map { it.status }.toSet()
            state.availableStatusTypes
                .filter { it.type in presentTypes }
                .map { it.type to it.label }
        }
        StatusesFilterSheet(
            currentFrom = state.filterDateFrom,
            currentTo = state.filterDateTo,
            currentSelectedEmployeeIds = state.filterEmployeeIds,
            currentSelectedStatusTypes = state.filterStatusTypes,
            employees = employees,
            statusTypes = statusTypes,
            onApply = { from, to, ids, types -> onIntent(StatusesIntent.OnApplyFilter(from, to, ids, types)) },
            onClear = { onIntent(StatusesIntent.OnClearFilter) },
            onDismiss = { onIntent(StatusesIntent.OnDismissFilterSheet) }
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun StatusesContentEmptyPreview() {
    CoreTheme {
        StatusesContent(
            state = StatusesState(),
            onIntent = {},
            snackbarHost = { SnackbarHost(hostState = androidx.compose.material3.SnackbarHostState()) }
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun StatusesContentLoadingPreview() {
    CoreTheme {
        StatusesContent(
            state = StatusesState(isLoading = true),
            onIntent = {},
            snackbarHost = { SnackbarHost(hostState = androidx.compose.material3.SnackbarHostState()) }
        )
    }
}

@Preview(showBackground = true, name = "Content State")
@Composable
private fun StatusesContentPreview() {
    val sampleStatuses = listOf(
        EmployeeStatusUi(
            id = "1",
            employeeId = "emp_1",
            employeeFullName = "Іванов Іван",
            status = "Office",
            note = "Робота в офісі",
            startTime = System.currentTimeMillis() - 30_000,
            endTime = null,
            isSynced = true
        ),
        EmployeeStatusUi(
            id = "2",
            employeeId = "emp_1",
            employeeFullName = "Іванов Іван",
            status = "Remote",
            note = null,
            startTime = System.currentTimeMillis() - 90_000,
            endTime = null,
            isSynced = true
        )
    )

    CoreTheme {
        StatusesContent(
            state = StatusesState(
                statuses = sampleStatuses,
                employeeGroups = listOf(
                    EmployeeStatusesGroup(
                        employeeId = "emp_1",
                        employeeName = "Іванов Іван",
                        statuses = sampleStatuses,
                        isExpanded = true
                    )
                )
            ),
            onIntent = {},
            snackbarHost = { SnackbarHost(hostState = androidx.compose.material3.SnackbarHostState()) }
        )
    }
}
