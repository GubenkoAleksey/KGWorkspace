package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.presentation.theme.CoreTheme

@Composable
fun DeleteStatusDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Видалення статусу") },
        text = { Text(text = "Ви впевнені, що хочете видалити цей статус? Дію неможливо скасувати.") },
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
private fun DeleteStatusDialogPreview() {
    CoreTheme {
        DeleteStatusDialog(onConfirm = {}, onDismiss = {})
    }
}
