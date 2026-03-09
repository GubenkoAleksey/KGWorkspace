package com.hubenko.firestoreapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hubenko.firestoreapp.ui.theme.StatusOfficeLight

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusOfficeLight),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, fontSize = 18.sp, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryActionButtonPreview() {
    PrimaryActionButton(text = "Натисніть мене", onClick = {})
}
