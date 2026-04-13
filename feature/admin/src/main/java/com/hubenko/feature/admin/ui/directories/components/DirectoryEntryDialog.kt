package com.hubenko.feature.admin.ui.directories.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText

@Composable
fun DirectoryEntryDialog(
    title: String,
    keyLabel: String,
    labelLabel: String,
    initialKey: String = "",
    initialLabel: String = "",
    isKeyEditable: Boolean = true,
    valueLabel: String? = null,
    initialValue: String = "",
    onSave: (key: String, label: String, value: String) -> Unit,
    onDismiss: () -> Unit
) {
    var key by remember { mutableStateOf(initialKey) }
    var label by remember { mutableStateOf(initialLabel) }
    var value by remember { mutableStateOf(initialValue) }

    val isValueValid = valueLabel == null || value.toDoubleOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title) },
        text = {
            val secondaryText = MaterialTheme.colorScheme.secondaryText()
            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = secondaryText,
                unfocusedLabelColor = secondaryText,
                disabledLabelColor = secondaryText,
                focusedBorderColor = secondaryText,
                unfocusedBorderColor = secondaryText,
                disabledBorderColor = secondaryText,
                focusedPlaceholderColor = secondaryText,
                unfocusedPlaceholderColor = secondaryText,
                disabledPlaceholderColor = secondaryText,
                cursorColor = secondaryText,
                selectionColors = TextSelectionColors(
                    handleColor = Color.Transparent,
                    backgroundColor = secondaryText.copy(alpha = 0.3f)
                )
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = key,
                    onValueChange = { if (isKeyEditable) key = it },
                    label = { Text(keyLabel) },
                    singleLine = true,
                    enabled = isKeyEditable,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(labelLabel) },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                if (valueLabel != null) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text(valueLabel) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = value.isNotEmpty() && value.toDoubleOrNull() == null,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(key.trim(), label.trim(), value.trim()) },
                enabled = key.isNotBlank() && label.isNotBlank() && isValueValid
                        && (valueLabel == null || value.isNotBlank())
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
            onSave = { _, _, _ -> },
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
            onSave = { _, _, _ -> },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun DirectoryEntryDialogWithValuePreview() {
    CoreTheme {
        DirectoryEntryDialog(
            title = "Додати тариф",
            keyLabel = "ID тарифу",
            labelLabel = "Назва",
            valueLabel = "Значення (грн/год)",
            initialKey = "RATE_200",
            initialLabel = "200 грн/год",
            initialValue = "200.0",
            isKeyEditable = false,
            onSave = { _, _, _ -> },
            onDismiss = {}
        )
    }
}
