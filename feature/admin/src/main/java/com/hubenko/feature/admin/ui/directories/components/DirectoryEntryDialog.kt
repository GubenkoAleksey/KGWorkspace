package com.hubenko.feature.admin.ui.directories.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme

@Composable
fun DirectoryEntryDialog(
    title: String,
    keyLabel: String,
    labelLabel: String,
    initialKey: String = "",
    initialLabel: String = "",
    isKeyEditable: Boolean = true,
    onSave: (key: String, label: String) -> Unit,
    onDismiss: () -> Unit
) {
    var key by remember { mutableStateOf(initialKey) }
    var label by remember { mutableStateOf(initialLabel) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = key,
                    onValueChange = { if (isKeyEditable) key = it },
                    label = { Text(keyLabel) },
                    singleLine = true,
                    enabled = isKeyEditable,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(labelLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(key.trim(), label.trim()) },
                enabled = key.isNotBlank() && label.isNotBlank()
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}

@Preview
@Composable
private fun DirectoryEntryDialogAddPreview() {
    CoreTheme {
        DirectoryEntryDialog(
            title = "Додати тип статусу",
            keyLabel = "Ключ (тип)",
            labelLabel = "Назва",
            onSave = { _, _ -> },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun DirectoryEntryDialogEditPreview() {
    CoreTheme {
        DirectoryEntryDialog(
            title = "Редагувати роль",
            keyLabel = "ID",
            labelLabel = "Назва",
            initialKey = "USER",
            initialLabel = "Працівник",
            isKeyEditable = false,
            onSave = { _, _ -> },
            onDismiss = {}
        )
    }
}
