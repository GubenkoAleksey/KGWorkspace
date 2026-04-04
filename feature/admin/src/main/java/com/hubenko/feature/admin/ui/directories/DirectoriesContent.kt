package com.hubenko.feature.admin.ui.directories

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
import com.hubenko.domain.model.Role
import com.hubenko.domain.model.StatusType
import com.hubenko.feature.admin.ui.directories.components.DirectoryEntryDialog
import com.hubenko.feature.admin.ui.directories.components.DirectoryItemRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoriesContent(
    state: DirectoriesState,
    onIntent: (DirectoriesIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Довідники",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    DirectorySectionHeader(
                        title = "Типи статусів",
                        onAdd = { onIntent(DirectoriesIntent.OnAddStatusTypeClick) }
                    )
                }
                items(state.statusTypes, key = { it.type }) { item ->
                    DirectoryItemRow(
                        label = item.label,
                        keyValue = item.type,
                        onEdit = { onIntent(DirectoriesIntent.OnEditStatusTypeClick(item)) },
                        onDelete = { onIntent(DirectoriesIntent.OnDeleteStatusTypeClick(item)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    DirectorySectionHeader(
                        title = "Ролі користувачів",
                        onAdd = { onIntent(DirectoriesIntent.OnAddRoleClick) }
                    )
                }
                items(state.roles, key = { it.id }) { item ->
                    DirectoryItemRow(
                        label = item.label,
                        keyValue = item.id,
                        onEdit = { onIntent(DirectoriesIntent.OnEditRoleClick(item)) },
                        onDelete = { onIntent(DirectoriesIntent.OnDeleteRoleClick(item)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }

    when (val dialog = state.dialog) {
        is DirectoryDialog.EditStatusType -> DirectoryEntryDialog(
            title = if (dialog.item == null) "Додати тип статусу" else "Редагувати тип статусу",
            keyLabel = "Ключ (тип)",
            labelLabel = "Назва",
            initialKey = dialog.item?.type ?: "",
            initialLabel = dialog.item?.label ?: "",
            isKeyEditable = dialog.item == null,
            onSave = { key, label -> onIntent(DirectoriesIntent.OnSaveStatusType(key, label)) },
            onDismiss = { onIntent(DirectoriesIntent.OnDismissDialog) }
        )

        is DirectoryDialog.EditRole -> DirectoryEntryDialog(
            title = if (dialog.item == null) "Додати роль" else "Редагувати роль",
            keyLabel = "ID ролі",
            labelLabel = "Назва",
            initialKey = dialog.item?.id ?: "",
            initialLabel = dialog.item?.label ?: "",
            isKeyEditable = dialog.item == null,
            onSave = { key, label -> onIntent(DirectoriesIntent.OnSaveRole(key, label)) },
            onDismiss = { onIntent(DirectoriesIntent.OnDismissDialog) }
        )

        is DirectoryDialog.ConfirmDeleteStatusType -> AlertDialog(
            onDismissRequest = { onIntent(DirectoriesIntent.OnDismissDialog) },
            title = { Text("Видалити тип статусу?") },
            text = { Text("«${dialog.label}» буде видалено з Firebase.") },
            confirmButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnConfirmDeleteStatusType(dialog.type)) }) {
                    Text("Видалити", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnDismissDialog) }) {
                    Text("Скасувати")
                }
            }
        )

        is DirectoryDialog.ConfirmDeleteRole -> AlertDialog(
            onDismissRequest = { onIntent(DirectoriesIntent.OnDismissDialog) },
            title = { Text("Видалити роль?") },
            text = { Text("«${dialog.label}» буде видалено з Firebase.") },
            confirmButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnConfirmDeleteRole(dialog.id)) }) {
                    Text("Видалити", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnDismissDialog) }) {
                    Text("Скасувати")
                }
            }
        )

        null -> Unit
    }
}

@Composable
private fun DirectorySectionHeader(
    title: String,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onAdd) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Додати",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true, name = "With data")
@Composable
private fun DirectoriesContentPreview() {
    CoreTheme {
        DirectoriesContent(
            state = DirectoriesState(
                statusTypes = listOf(
                    StatusType("Office", "В офісі"),
                    StatusType("Remote", "Віддалено"),
                    StatusType("Sick", "Лікарняний")
                ),
                roles = listOf(
                    Role("USER", "Працівник"),
                    Role("ADMIN", "Адміністратор")
                )
            ),
            onIntent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun DirectoriesContentLoadingPreview() {
    CoreTheme {
        DirectoriesContent(
            state = DirectoriesState(isLoading = true),
            onIntent = {},
            onBackClick = {}
        )
    }
}
