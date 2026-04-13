package com.hubenko.feature.status.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.presentation.theme.CoreTheme

@Composable
fun SubmitConfirmDialog(
    status: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Підтвердження відправки") },
        text = { Text("Ви дійсно хочете відправити статус \"$status\"?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Відправити")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SubmitConfirmDialogPreview() {
    CoreTheme {
        SubmitConfirmDialog(
            status = "В офісі",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
