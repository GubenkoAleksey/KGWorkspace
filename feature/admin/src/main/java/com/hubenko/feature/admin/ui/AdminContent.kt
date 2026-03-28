package com.hubenko.feature.admin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.feature.admin.ui.components.DeleteStatusesDialog
import com.hubenko.feature.admin.ui.components.EmployeeDialog
import com.hubenko.feature.admin.ui.components.EmployeeItem
import com.hubenko.feature.admin.ui.components.StatusItem

/**
 * Stateless Composable, що відображає контент панелі адміністратора.
 *
 * @param state Поточний стан екрана.
 * @param onIntent Лямбда для надсилання інтентів у ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContent(
    state: AdminState,
    onIntent: (AdminIntent) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = state.selectedTab.title,
                onBackClick = { onIntent(AdminIntent.OnBackClick) },
                actions = {
                    if (state.selectedTab == AdminTab.STATUSES && state.statuses.isNotEmpty()) {
                        IconButton(onClick = { onIntent(AdminIntent.OnDeleteAllStatusesClick) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Видалити всі статуси",
                                tint = Color.Red
                            )
                        }
                        IconButton(onClick = { onIntent(AdminIntent.OnExportStatusesClick) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Експортувати CSV"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.selectedTab == AdminTab.EMPLOYEES) {
                FloatingActionButton(onClick = { onIntent(AdminIntent.OnAddEmployeeClick) }) {
                    Icon(Icons.Default.Add, contentDescription = "Додати працівника")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.selectedTab != AdminTab.DASHBOARD) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (state.selectedTab) {
                    AdminTab.DASHBOARD -> DashboardContent(onIntent)
                    AdminTab.EMPLOYEES -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.employees) { employee ->
                                EmployeeItem(
                                    employee = employee,
                                    onEdit = { onIntent(AdminIntent.OnEditEmployeeClick(employee)) },
                                    onDelete = { onIntent(AdminIntent.OnDeleteEmployeeClick(employee.id)) }
                                )
                            }
                        }
                    }
                    AdminTab.STATUSES -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.statuses) { status ->
                                StatusItem(status = status)
                            }
                        }
                    }
                    AdminTab.SCHEDULE -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.employees) { employee ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onIntent(AdminIntent.OnEmployeeSelectedForSchedule(employee.id)) },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                        Text(
                                            text = "${employee.lastName} ${employee.firstName}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.isEmployeeDialogOpen) {
        EmployeeDialog(
            employee = state.editingEmployee,
            onDismiss = { onIntent(AdminIntent.OnDismissDialog) },
            onSave = { onIntent(AdminIntent.OnSaveEmployee(it)) }
        )
    }

    if (state.isDeleteStatusesDialogOpen) {
        DeleteStatusesDialog(
            onConfirm = { onIntent(AdminIntent.OnConfirmDeleteAllStatuses) },
            onDismiss = { onIntent(AdminIntent.OnDismissDialog) }
        )
    }
}

@Composable
fun DashboardContent(onIntent: (AdminIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DashboardButton(
            title = "Працівники",
            icon = Icons.Default.People,
            onClick = { onIntent(AdminIntent.OnTabSelected(AdminTab.EMPLOYEES)) }
        )
        DashboardButton(
            title = "Розклад",
            icon = Icons.Default.Schedule,
            onClick = { onIntent(AdminIntent.OnTabSelected(AdminTab.SCHEDULE)) }
        )
        DashboardButton(
            title = "Статуси",
            icon = Icons.Default.TaskAlt,
            onClick = { onIntent(AdminIntent.OnTabSelected(AdminTab.STATUSES)) }
        )
    }
}

@Composable
fun DashboardButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
