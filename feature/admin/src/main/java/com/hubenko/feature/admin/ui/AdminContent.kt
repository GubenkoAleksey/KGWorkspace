package com.hubenko.feature.admin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.feature.admin.ui.components.DeleteStatusesDialog
import com.hubenko.feature.admin.ui.components.EmployeeDialog
import com.hubenko.feature.admin.ui.components.EmployeeItem
import com.hubenko.feature.admin.ui.components.StatusItem

/**
 * Stateless Composable, що відображає контент панелі адміністратора.
 * Містить перемикач вкладок (Працівники/Статуси) та відповідні списки.
 *
 * @param state Поточний стан екрана.
 * @param onIntent Лямбда для надсилання інтентів у ViewModel.
 * @param onBack Лямбда для обробки натиску "Назад".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContent(
    state: AdminState,
    onIntent: (AdminIntent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Панель адміністратора",
                onBackClick = onBack,
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
            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                AdminTab.entries.forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { onIntent(AdminIntent.OnTabSelected(tab)) },
                        text = { Text(tab.title) }
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (state.selectedTab) {
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
