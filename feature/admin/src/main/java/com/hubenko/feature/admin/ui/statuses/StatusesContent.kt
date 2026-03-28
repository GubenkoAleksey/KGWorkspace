package com.hubenko.feature.admin.ui.statuses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.feature.admin.ui.statuses.components.DeleteStatusesDialog
import com.hubenko.feature.admin.ui.statuses.components.EmployeeStatusesItem
import com.hubenko.domain.model.EmployeeStatus

/**
 * Stateless Composable для екрана статусів співробітників.
 *
 * @param state Поточний стан екрана.
 * @param onIntent Лямбда для надсилання інтентів у ViewModel.
 * @param onBackClick Callback для повернення на Dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusesContent(
    state: StatusesState,
    onIntent: (StatusesIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Статуси працівників",
                onBackClick = onBackClick,
                actions = {
                    if (state.employeeGroups.isNotEmpty()) {
                        IconButton(onClick = { onIntent(StatusesIntent.OnDeleteAllClick) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Видалити всі статуси",
                                tint = Color.Red
                            )
                        }
                        IconButton(onClick = { onIntent(StatusesIntent.OnExportClick) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Експортувати CSV"
                            )
                        }
                    }
                }
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
            if (state.employeeGroups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Статуси відсутні")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.employeeGroups,
                        key = { it.employeeId }
                    ) { group ->
                        EmployeeStatusesItem(
                            group = group,
                            onToggleExpand = {
                                onIntent(StatusesIntent.OnEmployeeExpandToggle(group.employeeId))
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.isDeleteDialogOpen) {
        DeleteStatusesDialog(
            onConfirm = { onIntent(StatusesIntent.OnConfirmDelete) },
            onDismiss = { onIntent(StatusesIntent.OnDismissDialog) }
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
            onBackClick = {}
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
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Content State")
@Composable
private fun StatusesContentPreview() {
    val sampleStatuses = listOf(
        EmployeeStatus(
            id = "1",
            employeeId = "emp_1",
            employeeFullName = "Іванов Іван",
            status = "Office",
            note = "Робота в офісі",
            startTime = System.currentTimeMillis() - 30_000,
            endTime = null,
            isSynced = true
        ),
        EmployeeStatus(
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
            onBackClick = {}
        )
    }
}

