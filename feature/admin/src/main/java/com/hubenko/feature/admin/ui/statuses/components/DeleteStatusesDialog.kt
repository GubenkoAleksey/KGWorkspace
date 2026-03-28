package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.ui.theme.CoreTheme

@Composable
fun DeleteStatusesDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Видалення всіх статусів") },
        text = {
            Text(
                text = "Ви впевнені, що хочете видалити всі статуси? Ця дія видалить дані як локально, " +
                        "так і з хмарного сховища (Firestore). Відмінити цю дію буде неможливо."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Видалити", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Скасувати")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteStatusesDialogPreview() {
    CoreTheme {
        DeleteStatusesDialog(onConfirm = {}, onDismiss = {})
    }
}

