package com.hubenko.feature.status.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText

@Composable
fun NoteInputField(
    note: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val maxLength = 300

    Column(modifier = modifier) {
        val activeColor = MaterialTheme.colorScheme.secondaryText()
        OutlinedTextField(
            value = note,
            onValueChange = {
                if (it.length <= maxLength) {
                    onNoteChange(it)
                }
            },
            label = { Text("Примітка (необов'язково)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            maxLines = 5,
            minLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = activeColor,
                focusedLabelColor = activeColor,
                unfocusedLabelColor = activeColor,
                cursorColor = activeColor
            )
        )
        Text(
            text = "${note.length}/$maxLength",
            style = MaterialTheme.typography.bodySmall,
            color = if (note.length >= maxLength) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryText(),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp, end = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteInputFieldPreview() {
    CoreTheme {
        NoteInputField(
            note = "Це приклад примітки.",
            onNoteChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
